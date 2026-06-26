package com.abhisheksingh.loanaxisapi.feature.loan.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PayInstallmentResponse {
    private int installmentNumber;
    private BigDecimal amount;
    private LocalDateTime paidDate;
    private BigDecimal remainingBalance;
    private LocalDate nextDueDate;
}
