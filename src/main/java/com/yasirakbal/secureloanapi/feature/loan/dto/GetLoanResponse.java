package com.yasirakbal.secureloanapi.feature.loan.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GetLoanResponse {
    private Long id;
    private BigDecimal principalAmount;
    private BigDecimal remainingBalance;
    private List<GetLoanResponseInstallmentsView> installments;
}
