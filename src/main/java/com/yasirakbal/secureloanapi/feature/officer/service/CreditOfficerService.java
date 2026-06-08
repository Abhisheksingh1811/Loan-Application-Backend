package com.yasirakbal.secureloanapi.feature.officer.service;

import com.yasirakbal.secureloanapi.feature.application.entity.LoanApplication;
import com.yasirakbal.secureloanapi.feature.application.service.LoanApplicationService;
import com.yasirakbal.secureloanapi.feature.audit.annotation.Auditable;
import com.yasirakbal.secureloanapi.feature.audit.enums.AuditEventType;
import com.yasirakbal.secureloanapi.feature.installment.entity.Installment;
import com.yasirakbal.secureloanapi.feature.installment.service.InstallmentService;
import com.yasirakbal.secureloanapi.feature.loan.entity.Loan;
import com.yasirakbal.secureloanapi.feature.loan.enums.LoanStatusType;
import com.yasirakbal.secureloanapi.feature.loan.service.LoanService;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class CreditOfficerService {
    private LoanApplicationService loanApplicationService;
    private LoanService loanService;
    private EntityManager entityManager;
    private InstallmentService installmentService;

    @Transactional
    @Auditable(eventType = AuditEventType.LOAN_APPROVED)
    public Loan approveLoanApplication(Long loanAppId,Long officerId) {
        LoanApplication approvedLoanApp = loanApplicationService.makeLoanApplicationStatusApproved(loanAppId,officerId);

        Loan loanToCreate = buildLoan(approvedLoanApp);
        List<Installment> installments = installmentService.calculateInstallments(loanToCreate);
        loanToCreate.setInstallments(installments);

        Loan createdLoan = loanService.createLoan(loanToCreate);

        //audit log

        return createdLoan;
    }

    @Transactional
    @Auditable(eventType = AuditEventType.LOAN_REJECTED)
    public void rejectLoanApplication(Long loanAppId,Long officerId, String rejectionReason) {
        loanApplicationService.makeLoanApplicationStatusRejected(loanAppId,officerId, rejectionReason);
    }

    private Loan buildLoan(LoanApplication loanApp) {
        LocalDate currentDate = LocalDate.now();
        return Loan.builder()
                .applicationId(loanApp.getId())
                .customer(loanApp.getCustomer())
                .loanType(loanApp.getLoanType())
                .principalAmount(loanApp.getRequestedAmount())
                .interestRate(loanApp.getInterestRate())
                .termMonths(loanApp.getRequestedTerm())
                .monthlyInstallment(loanApp.getMonthlyInstallment())
                .totalAmount(loanApp.getTotalPayment())
                .status(LoanStatusType.ACTIVE)
                .remainingBalance(loanApp.getTotalPayment())
                .nextDueDate(currentDate.plusMonths(1))
                .disbursementDate(currentDate)
                .maturityDate(currentDate.plusMonths(loanApp.getRequestedTerm()))
                .build();
    }
}
