package com.abhisheksingh.loanaxisapi.feature.application.dto;

import com.abhisheksingh.loanaxisapi.feature.application.enums.LoanApplicationStatus;
import com.abhisheksingh.loanaxisapi.feature.loan.enums.LoanType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateLoanApplicationResponse {
    private Long id;
    private LoanType loanType;
    private BigDecimal requestedAmount;
    private Integer requestedTerm;
    private BigDecimal monthlyInstallment;
    private BigDecimal interestRate;
    private BigDecimal totalPayment;
    private BigDecimal dtiRatio;
    private LoanApplicationStatus status;
    private List<String> rejectionReasons;
    private LocalDateTime createdAt;
}
