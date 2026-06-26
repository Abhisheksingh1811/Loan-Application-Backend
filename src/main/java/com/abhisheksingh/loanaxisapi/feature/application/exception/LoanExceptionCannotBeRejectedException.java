package com.abhisheksingh.loanaxisapi.feature.application.exception;

import com.abhisheksingh.loanaxisapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class LoanExceptionCannotBeRejectedException extends BusinessException {
    public LoanExceptionCannotBeRejectedException() {
        super("Only the pending applications can be rejected", HttpStatus.BAD_REQUEST);
    }
}
