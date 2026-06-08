package com.yasirakbal.secureloanapi.feature.officer.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ApproveLoanApplicationResponse {
    private Long loanId;
    private BigDecimal principalAmount;
    private Integer termMonths;
    private BigDecimal monthlyInstallment;
    private BigDecimal totalAmount;
    private LocalDate disbursementDate;
    private LocalDate maturityDate;
}
