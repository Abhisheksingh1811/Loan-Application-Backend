package com.abhisheksingh.loanaxisapi.common.exception;

import org.springframework.http.HttpStatus;

public abstract class NonRollbackBusinessException extends BusinessException {

    protected NonRollbackBusinessException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    protected NonRollbackBusinessException(String message, HttpStatus httpStatus, Throwable cause) {
        super(message, httpStatus, cause);
    }
}
