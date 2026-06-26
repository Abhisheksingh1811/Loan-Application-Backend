package com.abhisheksingh.loanaxisapi.feature.loan.dto;

import com.abhisheksingh.loanaxisapi.feature.loan.enums.LoanStatusType;
import com.abhisheksingh.loanaxisapi.feature.loan.enums.LoanType;
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
