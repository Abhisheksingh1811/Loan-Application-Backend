package com.yasirakbal.secureloanapi.feature.application.dto;

import com.yasirakbal.secureloanapi.feature.application.enums.LoanApplicationStatus;
import com.yasirakbal.secureloanapi.feature.loan.enums.LoanType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GetCustomersApplicationsResponse {
    private Long id;
    private LoanType loanType;
    private BigDecimal requestedAmount;
    private LoanApplicationStatus status;
    private LocalDateTime createdAt;
}
