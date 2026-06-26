package com.abhisheksingh.loanaxisapi.feature.application.exception;

import com.abhisheksingh.loanaxisapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class LoanExceptionCannotBeApprovedException extends BusinessException {
    public LoanExceptionCannotBeApprovedException() {
        super("Only the pending applications can be approved", HttpStatus.BAD_REQUEST);
    }
}
