package com.abhisheksingh.loanaxisapi.feature.loan.exception;

import com.abhisheksingh.loanaxisapi.common.exception.BusinessException;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class InstallmentPaymentException extends BusinessException {
    public InstallmentPaymentException(List<BusinessException> validationErrors) {
        this(validationErrors, "User creation failed", HttpStatus.CONFLICT);
    }

    public InstallmentPaymentException(List<BusinessException> creationErrors, String message, HttpStatus statusCode) {
        super(message, statusCode);

        List<InstallmentPaymentException.InstallmentPaymentError> errors = creationErrors.stream()
                .map(ex -> InstallmentPaymentException.InstallmentPaymentError.builder()
                        .errorType(ex.getClass().getSimpleName())
                        .errorMessage(ex.getMessage())
                        .additionalInfo(ex.getDetails().isEmpty() ? null : ex.getDetails())
                        .build())
                .toList();

        addDetail("errorCount", errors.size());
        addDetail("errors", errors);
    }

    @Data
    @Builder
    public static class InstallmentPaymentError {
        private String errorType;
        private String errorMessage;
        private Map<String, Object> additionalInfo;
    }
}
