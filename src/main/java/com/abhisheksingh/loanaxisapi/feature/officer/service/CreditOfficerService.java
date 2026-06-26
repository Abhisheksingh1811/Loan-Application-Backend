package com.abhisheksingh.loanaxisapi.feature.officer.service;

import com.abhisheksingh.loanaxisapi.feature.application.entity.LoanApplication;
import com.abhisheksingh.loanaxisapi.feature.application.service.LoanApplicationService;
import com.abhisheksingh.loanaxisapi.feature.audit.annotation.Auditable;
import com.abhisheksingh.loanaxisapi.feature.audit.enums.AuditEventType;
import com.abhisheksingh.loanaxisapi.feature.installment.entity.Installment;
import com.abhisheksingh.loanaxisapi.feature.installment.service.InstallmentService;
import com.abhisheksingh.loanaxisapi.feature.loan.entity.Loan;
import com.abhisheksingh.loanaxisapi.feature.loan.enums.LoanStatusType;
import com.abhisheksingh.loanaxisapi.feature.loan.service.LoanService;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import com.abhisheksingh.loanaxisapi.feature.outbox.service.OutboxService;
@Service
@AllArgsConstructor
public class CreditOfficerService {
    private LoanApplicationService loanApplicationService;
    private LoanService loanService;
    private EntityManager entityManager;
    private InstallmentService installmentService;
    private OutboxService outboxService;
    @Transactional
    @Auditable(eventType = AuditEventType.LOAN_APPROVED)
    public Loan approveLoanApplication(Long loanAppId, Long officerId) {
        LoanApplication approvedLoanApp =
                loanApplicationService.makeLoanApplicationStatusApproved(loanAppId, officerId);

        Loan loanToCreate = buildLoan(approvedLoanApp);
        List<Installment> installments = installmentService.calculateInstallments(loanToCreate);
        loanToCreate.setInstallments(installments);

        Loan createdLoan = loanService.createLoan(loanToCreate);

        String payload = """
            {
              "applicationId": %d,
              "loanId": %d,
              "customerEmail": "%s",
              "customerName": "%s",
              "loanType": "%s",
              "approvedAmount": "%s",
              "monthlyInstallment": "%s",
              "termMonths": %d
            }
            """.formatted(
                approvedLoanApp.getId(),
                createdLoan.getId(),
                approvedLoanApp.getCustomer().getEmail(),
                approvedLoanApp.getCustomer().getFullName(),
                approvedLoanApp.getLoanType().name(),
                approvedLoanApp.getRequestedAmount(),
                approvedLoanApp.getMonthlyInstallment(),
                approvedLoanApp.getRequestedTerm()
        );

        outboxService.saveEvent(
                "LOAN_APPROVED",
                "LoanApplication",
                approvedLoanApp.getId(),
                payload
        );

        return createdLoan;
    }

    @Transactional
    @Auditable(eventType = AuditEventType.LOAN_REJECTED)
    public void rejectLoanApplication(Long loanAppId, Long officerId, String rejectionReason) {
        LoanApplication rejectedLoanApp =
                loanApplicationService.makeLoanApplicationStatusRejected(loanAppId, officerId, rejectionReason);

        String payload = """
            {
              "applicationId": %d,
              "customerEmail": "%s",
              "customerName": "%s",
              "loanType": "%s",
              "requestedAmount": "%s",
              "rejectionReason": "%s"
            }
            """.formatted(
                rejectedLoanApp.getId(),
                rejectedLoanApp.getCustomer().getEmail(),
                rejectedLoanApp.getCustomer().getFullName(),
                rejectedLoanApp.getLoanType().name(),
                rejectedLoanApp.getRequestedAmount(),
                rejectionReason.replace("\"", "\\\"")
        );

        outboxService.saveEvent(
                "LOAN_REJECTED",
                "LoanApplication",
                rejectedLoanApp.getId(),
                payload
        );
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
