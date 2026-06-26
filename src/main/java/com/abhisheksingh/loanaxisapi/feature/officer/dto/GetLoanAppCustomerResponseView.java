package com.abhisheksingh.loanaxisapi.feature.officer.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GetLoanAppCustomerResponseView {
    private String fullName;
    private int creditScore;
    private BigDecimal monthlyIncome;
}
