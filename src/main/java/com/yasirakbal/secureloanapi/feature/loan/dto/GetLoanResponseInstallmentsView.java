package com.yasirakbal.secureloanapi.feature.loan.dto;

import com.yasirakbal.secureloanapi.feature.installment.enums.InstallmentStatus;
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
