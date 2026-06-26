package com.abhisheksingh.loanaxisapi.feature.loan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayInstallmentRequest {
    @NotNull
    @Positive
    private BigDecimal amount;

    @NotBlank
    private String paymentMethod;
}
