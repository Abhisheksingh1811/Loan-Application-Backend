package com.abhisheksingh.loanaxisapi.feature.application.exception;

import com.abhisheksingh.loanaxisapi.common.exception.BusinessException;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class LoanApplicationException extends BusinessException {
    public LoanApplicationException(List<BusinessException> creationErrors) {
        this(creationErrors, "User creation failed", HttpStatus.CONFLICT);
    }

    public LoanApplicationException(List<BusinessException> creationErrors, String message, HttpStatus statusCode) {
        super(message, statusCode);

        List<LoanApplicationException.LoanApplicationError> errors = creationErrors.stream()
                .map(ex -> LoanApplicationException.LoanApplicationError.builder()
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
    public static class LoanApplicationError {
        private String errorType;
        private String errorMessage;
        private Map<String, Object> additionalInfo;
    }
}
