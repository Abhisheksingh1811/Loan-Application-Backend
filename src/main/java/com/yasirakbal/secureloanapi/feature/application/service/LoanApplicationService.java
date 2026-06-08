package com.yasirakbal.secureloanapi.feature.application.service;

import com.yasirakbal.secureloanapi.common.exception.BusinessException;
import com.yasirakbal.secureloanapi.common.exception.ResourceNotFoundException;
import com.yasirakbal.secureloanapi.common.utils.DateUtils;
import com.yasirakbal.secureloanapi.feature.application.dto.CreateLoanApplicationRequest;
import com.yasirakbal.secureloanapi.feature.application.entity.LoanApplication;
import com.yasirakbal.secureloanapi.feature.application.enums.LoanApplicationStatus;
import com.yasirakbal.secureloanapi.feature.application.exception.*;
import com.yasirakbal.secureloanapi.feature.application.repository.LoanApplicationRepository;
import com.yasirakbal.secureloanapi.feature.audit.annotation.Auditable;
import com.yasirakbal.secureloanapi.feature.audit.enums.AuditEventType;
import com.yasirakbal.secureloanapi.feature.auth.exception.InvalidCredentialsException;
import com.yasirakbal.secureloanapi.feature.installment.service.InstallmentService;
import com.yasirakbal.secureloanapi.feature.loan.entity.Loan;
import com.yasirakbal.secureloanapi.feature.loan.enums.LoanStatusType;
import com.yasirakbal.secureloanapi.feature.loan.enums.LoanType;
import com.yasirakbal.secureloanapi.feature.loan.repository.LoanRepository;
import com.yasirakbal.secureloanapi.feature.user.entity.User;
import com.yasirakbal.secureloanapi.feature.user.enums.District;
import com.yasirakbal.secureloanapi.feature.user.service.UserService;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import com.yasirakbal.secureloanapi.feature.outbox.service.OutboxService;
import com.yasirakbal.secureloanapi.feature.installment.entity.Installment;

@Service
@AllArgsConstructor
public class LoanApplicationService {
    private LoanApplicationRepository loanApplicationRepository;
    private LoanRepository loanRepository;
    private UserService userService;
    private EntityManager entityManager;
    private InstallmentService installmentService;
    private OutboxService outboxService;

    @Transactional
    @Auditable(eventType = AuditEventType.LOAN_APPLICATION_CREATED, resource = "#customerId")
    public LoanApplication createApplication(CreateLoanApplicationRequest request, Long customerId) {
        User customer = userService.getUserById(customerId);
        LoanType requestedLoanType = request.getLoanType();
        BigDecimal requestedAmount = request.getRequestedAmount();
        String purpose = request.getPurpose();
        int requestedTerm = request.getRequestedTerm();
        BigDecimal interestRate = requestedLoanType.getMonthlyInterestRate();

        List<BusinessException> creationErrors = new ArrayList<>();

        if(!requestedLoanType.checkIfItsEligible(customer.getMonthlyIncome())) {
            creationErrors.add(new MonthlyIncomeNotEnoughException());
        }

        LocalDate dateWhenCreditWillBeFinished = LocalDate.now().plusMonths(requestedTerm);
        int customerAgeAfterCreditFinished = Period.between(customer.getBirthDate(), dateWhenCreditWillBeFinished).getYears();
        if(customerAgeAfterCreditFinished > 65) {
            creationErrors.add(new CustomerAgeRequestedTermNotEligibleException(65, customerAgeAfterCreditFinished));
        }

        BigDecimal dti = getDti(customerId, customer.getMonthlyIncome(),
                requestedAmount, requestedTerm);
        if(dti.compareTo(BigDecimal.valueOf(40)) > 0)
        {
            creationErrors.add(new DtiNotEligibleException(BigDecimal.valueOf(40), dti));
        }

        LoanApplicationStatus applicationStatusWithCreditScore = determineApplicationStatusWithCreditScore(customer.getCreditScore());
        if(applicationStatusWithCreditScore.equals(LoanApplicationStatus.AUTO_REJECTED)) {
            creationErrors.add(new CustomerCreditScoreNotEligible(500));
        }


        BigDecimal monthlyInstallment = installmentService.calculateMonthlyInstallment(requestedAmount,
                request.getLoanType().getMonthlyInterestRate(), request.getRequestedTerm());
        BigDecimal totalPayment = monthlyInstallment.multiply(BigDecimal.valueOf(requestedTerm));
        if(!creationErrors.isEmpty()) { //rejection
            var rejectedLoanApp = buildForRejectedCase(customerId, requestedLoanType, requestedAmount,
                    requestedTerm, purpose, monthlyInstallment, interestRate, totalPayment, dti, creationErrors);

            LoanApplication savedRejectedLoanApp = loanApplicationRepository.save(rejectedLoanApp);

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
                    savedRejectedLoanApp.getId(),
                    customer.getEmail(),
                    customer.getFullName(),
                    savedRejectedLoanApp.getLoanType().name(),
                    savedRejectedLoanApp.getRequestedAmount(),
                    savedRejectedLoanApp.getRejectionReasons().replace("\"", "\\\"")
            );

            outboxService.saveEvent(
                    "LOAN_AUTO_REJECTED",
                    "LoanApplication",
                    savedRejectedLoanApp.getId(),
                    payload
            );

            return savedRejectedLoanApp;
        }


        var createdLoanApp = buildForSuccessCase(customerId, requestedLoanType, requestedAmount,
                requestedTerm, purpose, monthlyInstallment, interestRate, totalPayment, dti, applicationStatusWithCreditScore);
        LoanApplication savedLoanApp = loanApplicationRepository.save(createdLoanApp);

        if (savedLoanApp.getStatus().equals(LoanApplicationStatus.APPROVED)) {
            Loan loanToCreate = buildLoanFromApplication(savedLoanApp);

            List<Installment> installments = installmentService.calculateInstallments(loanToCreate);
            loanToCreate.setInstallments(installments);

            Loan createdLoan = loanRepository.save(loanToCreate);

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
                    savedLoanApp.getId(),
                    createdLoan.getId(),
                    customer.getEmail(),
                    customer.getFullName(),
                    savedLoanApp.getLoanType().name(),
                    savedLoanApp.getRequestedAmount(),
                    savedLoanApp.getMonthlyInstallment(),
                    savedLoanApp.getRequestedTerm()
            );

            outboxService.saveEvent(
                    "LOAN_AUTO_APPROVED",
                    "LoanApplication",
                    savedLoanApp.getId(),
                    payload
            );
        }

        return savedLoanApp;
    }
    private Loan buildLoanFromApplication(LoanApplication loanApp) {
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
    private LoanApplicationStatus determineApplicationStatusWithCreditScore(int creditScore) {
        if(creditScore < 500) {
            return LoanApplicationStatus.AUTO_REJECTED;
        } else if(creditScore < 750) {
            return LoanApplicationStatus.PENDING;
        } else {
            return LoanApplicationStatus.APPROVED;
        }
    }

    private LoanApplication buildForRejectedCase(Long customerId, LoanType requestedLoanType, BigDecimal requestedAmount,
                                                 int requestedTerm, String purpose, BigDecimal monthlyInstallment,
                                                 BigDecimal interestRate, BigDecimal totalPayment, BigDecimal dti, List<BusinessException> creationErrors) {
        return build(customerId, requestedLoanType, requestedAmount, requestedTerm, purpose, monthlyInstallment, interestRate, totalPayment, dti, creationErrors, LoanApplicationStatus.AUTO_REJECTED);
    }

    private LoanApplication buildForSuccessCase(Long customerId, LoanType requestedLoanType, BigDecimal requestedAmount, int requestedTerm, String purpose, BigDecimal monthlyInstallment, BigDecimal interestRate, BigDecimal totalPayment, BigDecimal dti, LoanApplicationStatus status) {
        return build(customerId, requestedLoanType, requestedAmount, requestedTerm, purpose, monthlyInstallment, interestRate, totalPayment, dti, null, status);
    }


    private LoanApplication build(Long customerId, LoanType requestedLoanType, BigDecimal requestedAmount, int requestedTerm,
                                  String purpose, BigDecimal monthlyInstallment, BigDecimal interestRate,
                                  BigDecimal totalPayment, BigDecimal dti, List<BusinessException> creationErrors, LoanApplicationStatus status) {
        User customerRef = entityManager.getReference(User.class, customerId);

        var rejectedLoanApp = LoanApplication.builder()
                .customer(customerRef)
                .loanType(requestedLoanType)
                .requestedAmount(requestedAmount)
                .requestedTerm(requestedTerm)
                .purpose(purpose)
                .monthlyInstallment(monthlyInstallment)
                .interestRate(interestRate)
                .totalPayment(totalPayment)
                .dtiRatio(dti)
                .status(status)
                .evaluatedAt(LocalDateTime.now())
                .rejectionReasons(creationErrors == null ? null : String.join("; ", creationErrors.stream().map(Throwable::getMessage).toList()))
                .build();
        return rejectedLoanApp;
    }


    public BigDecimal getDti(Long customerId, BigDecimal monthlyIncome,
                            BigDecimal newLoanAmount, Integer installmentCount) {

        BigDecimal newMonthlyInstallment = newLoanAmount
                .divide(BigDecimal.valueOf(installmentCount), 2, RoundingMode.HALF_UP);

        BigDecimal maxMonthlyPayment = calculateMaxMonthlyPayment(customerId, newMonthlyInstallment);

        BigDecimal dti = maxMonthlyPayment
                .divide(monthlyIncome, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        return dti;
    }

    public BigDecimal calculateMaxMonthlyPayment(
            Long customerId,
            BigDecimal newLoanMonthlyInstallment) {

        // Tüm aktif kredilerin aylık taksitlerini topla
        List<Loan> activeLoans = loanRepository.findByCustomerIdAndStatus(
                customerId,
                LoanStatusType.ACTIVE
        );

        BigDecimal totalExistingInstallments = activeLoans.stream()
                .map(Loan::getMonthlyInstallment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Yeni kredi taksitini ekle
        return totalExistingInstallments.add(newLoanMonthlyInstallment);
    }


    @Transactional(readOnly = true)
    public List<LoanApplication> getCustomersApplications(Long customerId, LoanApplicationStatus status) {
        return status == null
                ? loanApplicationRepository.findByCustomerId(customerId)
                : loanApplicationRepository.findByCustomerIdAndStatus(customerId, status);
    }

    @Transactional(readOnly = true)
    public LoanApplication getLoanApplicationById(Long id) {
        return loanApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LoanApplication", id));
    }

    @Transactional
    @Auditable(eventType = AuditEventType.LOAN_DELETED)
    public void deleteLoanApplication(Long id) {
        LoanApplication loanApplication = loanApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LoanApplication", id));

        if(!loanApplication.getStatus().equals(LoanApplicationStatus.PENDING)) {
            throw new LoanApplicationCannotBeDeletedException();
        }

        loanApplication.setStatus(LoanApplicationStatus.CANCELLED);
        loanApplicationRepository.save(loanApplication);
    }

    @Transactional(readOnly = true)
    public Page<LoanApplication> getAllLoanApplications(
            LoanApplicationStatus status,
            Integer page,
            Integer size,
            String sortBy,
            String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        return status == null
            ? loanApplicationRepository.findAll(pageable)
            : loanApplicationRepository.findAllByStatus(status, pageable);
    }

    public Page<LoanApplication> getAllLoanApplicationsForOfficer(
            Long officerId,
            LoanApplicationStatus status,
            Integer page,
            Integer size,
            String sortBy,
            String direction
    ) {

        Sort.Direction sortDirection =
                direction.equalsIgnoreCase("ASC")
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(sortDirection, sortBy)
                );

        User officer =
                userService.getUserById(officerId);

        District district =
                officer.getDistrict();

        return status == null
                ? loanApplicationRepository.findAllByCustomerDistrict(
                district,
                pageable
        )
                : loanApplicationRepository
                .findAllByCustomerDistrictAndStatus(
                        district,
                        status,
                        pageable
                );
    }

    private void validateOfficerDistrictAccess(Long officerId, LoanApplication loanApplication) {
        User officer = userService.getUserById(officerId);

        if (officer.getDistrict() != loanApplication.getCustomer().getDistrict()) {
            throw new AccessDeniedException(
                    "Officer cannot access applications outside their district"
            );
        }
    }

    @Transactional
    @Auditable(eventType = AuditEventType.LOAN_APPROVED)
    public LoanApplication makeLoanApplicationStatusApproved(Long applicationId,Long officerId) {
        LoanApplication loanApplication = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("LoanApplication", applicationId));

        validateOfficerDistrictAccess(officerId, loanApplication);

        if(!loanApplication.getStatus().equals(LoanApplicationStatus.PENDING)) {
            throw new LoanExceptionCannotBeApprovedException();
        }

        loanApplication.setStatus(LoanApplicationStatus.APPROVED);
        return loanApplicationRepository.save(loanApplication);
    }

    @Transactional
    @Auditable(eventType = AuditEventType.LOAN_REJECTED)
    public LoanApplication makeLoanApplicationStatusRejected(Long applicationId,Long officerId, String rejectionReason) {
        LoanApplication loanApplication = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("LoanApplication", applicationId));

        validateOfficerDistrictAccess(officerId, loanApplication);

        if(!loanApplication.getStatus().equals(LoanApplicationStatus.PENDING)) {
            throw new LoanExceptionCannotBeApprovedException();
        }

        loanApplication.setRejectionReasons(rejectionReason);
        loanApplication.setStatus(LoanApplicationStatus.REJECTED);
        return loanApplicationRepository.save(loanApplication);
    }
}
