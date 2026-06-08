package com.yasirakbal.secureloanapi.feature.application.dto;

import com.yasirakbal.secureloanapi.feature.loan.enums.LoanType;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateLoanApplicationRequest {
    @NotNull(message = "Loan type is required")
    private LoanType loanType;

    @NotNull(message = "Requested amount is required")
    @Positive(message = "Amount must be positive")
    @Digits(integer = 12, fraction = 2, message = "Invalid amount format")
    private BigDecimal requestedAmount;

    @NotNull(message = "Requested term is required")
    @Positive(message = "Term must be positive")
    private Integer requestedTerm;

    @NotBlank(message = "Purpose is required")
    @Size(max = 500, message = "Purpose too long")
    private String purpose;
}
