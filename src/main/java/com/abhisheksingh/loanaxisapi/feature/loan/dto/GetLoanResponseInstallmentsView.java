package com.abhisheksingh.loanaxisapi.feature.loan.dto;

import com.abhisheksingh.loanaxisapi.feature.installment.enums.InstallmentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class GetLoanResponseInstallmentsView {
    private Long id;
    private Integer installmentNumber;
    private LocalDate dueDate;
    private BigDecimal amount;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private InstallmentStatus status;
    private LocalDateTime paidDate;
}
