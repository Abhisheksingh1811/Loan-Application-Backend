package com.abhisheksingh.loanaxisapi.feature.loan.service;

import com.abhisheksingh.loanaxisapi.common.exception.BusinessException;
import com.abhisheksingh.loanaxisapi.common.exception.ResourceNotFoundException;
import com.abhisheksingh.loanaxisapi.feature.audit.annotation.Auditable;
import com.abhisheksingh.loanaxisapi.feature.audit.enums.AuditEventType;
import com.abhisheksingh.loanaxisapi.feature.installment.entity.Installment;
import com.abhisheksingh.loanaxisapi.feature.installment.enums.InstallmentStatus;
import com.abhisheksingh.loanaxisapi.feature.installment.repository.InstallmentRepository;
import com.abhisheksingh.loanaxisapi.feature.loan.dto.PayInstallmentRequest;
import com.abhisheksingh.loanaxisapi.feature.loan.dto.PayInstallmentResponse;
import com.abhisheksingh.loanaxisapi.feature.loan.entity.Loan;
import com.abhisheksingh.loanaxisapi.feature.loan.enums.LoanStatusType;
import com.abhisheksingh.loanaxisapi.feature.loan.exception.AmountMismatchException;
import com.abhisheksingh.loanaxisapi.feature.loan.exception.InstallmentAlreadyPaidException;
import com.abhisheksingh.loanaxisapi.feature.loan.exception.InstallmentPaymentException;
import com.abhisheksingh.loanaxisapi.feature.loan.exception.PreviousInstallmentNotPaidException;
import com.abhisheksingh.loanaxisapi.feature.loan.repository.LoanRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class LoanService {
    private LoanRepository loanRepository;
    private InstallmentRepository installmentRepository;

    @Transactional
    @Auditable(eventType = AuditEventType.LOAN_CREATED, resource = "#loanToCreate.getApplicationId")
    public Loan createLoan(Loan loanToCreate) {
        return loanRepository.save(loanToCreate);
    }

    @Transactional(readOnly = true)
    public List<Loan> getCustomersLoans(Long customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public Loan getLoanById(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", loanId));
    }

    @Transactional
    @Auditable(eventType = AuditEventType.INSTALLMENT_PAID, resource = "#loanId" + ":" + "#installmentId")
    public PayInstallmentResponse payInstallment(Long loanId, Long installmentId, PayInstallmentRequest request) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", loanId));

        Installment installment = installmentRepository.findById(installmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Installment", installmentId));

        List<BusinessException> validationErrors = new ArrayList<>();

        if(installment.getStatus().equals(InstallmentStatus.PAID)) {
            validationErrors.add(new InstallmentAlreadyPaidException());
        }

        boolean isPrevInstallmentPaid = isPrevInstallmentPaid(loanId, installment.getInstallmentNumber());
        if(!isPrevInstallmentPaid) {
            validationErrors.add(new PreviousInstallmentNotPaidException());
        }

        if(!request.getAmount().equals(installment.getAmount())) {
            validationErrors.add(new AmountMismatchException());
        }

        if(!validationErrors.isEmpty()) {
            throw new InstallmentPaymentException(validationErrors);
        }

        //installment logic
        LocalDateTime paidDate = LocalDateTime.now();
        installment.setStatus(InstallmentStatus.PAID);
        installment.setPaidDate(paidDate);
        installmentRepository.save(installment);

        //loan logic
        BigDecimal remainingLoanBalance = loan.getRemainingBalance();
        loan.setRemainingBalance(remainingLoanBalance.subtract(installment.getPrincipalAmount()));
        loan.setPaidInstallments(loan.getPaidInstallments() + 1);
        loan.setNextDueDate(getNextDueDate(installmentId, installment.getInstallmentNumber(), loan.getTermMonths()));

        if(loan.getTermMonths() == installment.getInstallmentNumber()) {
            loan.setStatus(LoanStatusType.COMPLETED);
        }

        loanRepository.save(loan);

        return PayInstallmentResponse.builder()
                .installmentNumber(installment.getInstallmentNumber())
                .amount(request.getAmount())
                .paidDate(paidDate)
                .remainingBalance(loan.getRemainingBalance())
                .nextDueDate(loan.getNextDueDate())
                .build();
    }

    private boolean isPrevInstallmentPaid(long loanId, int installmentNumber) {
        if(installmentNumber == 1) return true;

        Installment installment = installmentRepository.findByLoanIdAndInstallmentNumber(loanId, installmentNumber-1)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", loanId)); //refactor

        return installment.getStatus().equals(InstallmentStatus.PAID);
    }

    private LocalDate getNextDueDate(long loanId, int installmentNumber, int termsMonth) {
        if(installmentNumber == termsMonth) {
            return null;
        }

        Installment installment = installmentRepository.findByLoanIdAndInstallmentNumber(loanId, installmentNumber + 1)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", loanId));

        return installment.getDueDate();
    }
}
