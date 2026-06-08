package com.yasirakbal.secureloanapi.feature.loan.dto;

import com.yasirakbal.secureloanapi.feature.loan.enums.LoanStatusType;
import com.yasirakbal.secureloanapi.feature.loan.enums.LoanType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GetMyLoansResponse {
    private Long id;
    private LoanType loanType;
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private Integer termMonths;
    private BigDecimal monthlyInstallment;
    private LoanStatusType status;
    private BigDecimal remainingBalance;
    private Integer paidInstallments;
    private LocalDate nextDueDate;
    private LocalDate disbursementDate;
    private LocalDate maturityDate;
}
