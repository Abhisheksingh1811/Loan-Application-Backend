package com.yasirakbal.secureloanapi.feature.loan.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public enum LoanType {
    PERSONAL(BigDecimal.valueOf(25000), BigDecimal.valueOf(0.0175)),
    VEHICLE(BigDecimal.valueOf(45000), BigDecimal.valueOf(0.0189)),
    MORTGAGE(BigDecimal.valueOf(55000), BigDecimal.valueOf(0.0125)),
    EDUCATION(BigDecimal.valueOf(15000), BigDecimal.valueOf(0.0150));

    private final BigDecimal minMonthlyIncome;
    private final BigDecimal monthlyInterestRate;

    public boolean checkIfItsEligible(BigDecimal customerMonthlyIncome) {
        return customerMonthlyIncome.compareTo(this.minMonthlyIncome) >= 0;
    }
}
