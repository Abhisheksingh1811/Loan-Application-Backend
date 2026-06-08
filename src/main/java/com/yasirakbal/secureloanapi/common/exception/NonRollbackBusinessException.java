package com.yasirakbal.secureloanapi.common.exception;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public abstract class NonRollbackBusinessException extends BusinessException {

    protected NonRollbackBusinessException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    protected NonRollbackBusinessException(String message, HttpStatus httpStatus, Throwable cause) {
        super(message, httpStatus, cause);
    }
}
