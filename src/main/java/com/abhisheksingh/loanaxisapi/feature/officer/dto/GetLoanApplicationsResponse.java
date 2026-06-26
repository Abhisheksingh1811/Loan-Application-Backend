package com.abhisheksingh.loanaxisapi.feature.officer.dto;

import com.abhisheksingh.loanaxisapi.feature.application.enums.LoanApplicationStatus;
import com.abhisheksingh.loanaxisapi.feature.loan.enums.LoanType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GetLoanApplicationsResponse {
    private Long id;
    private GetLoanAppCustomerResponseView customer;
    private LoanType loanType;
    private BigDecimal requestedAmount;
    private BigDecimal dtiRatio;
    private LoanApplicationStatus status;
    private LocalDateTime createdAt;
}
