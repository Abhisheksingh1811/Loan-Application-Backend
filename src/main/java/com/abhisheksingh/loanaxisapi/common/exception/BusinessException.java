package com.abhisheksingh.loanaxisapi.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class BusinessException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final Map<String, Object> details;

    protected BusinessException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.details = new HashMap<>();
    }

    protected BusinessException(String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.details = new HashMap<>();
    }

    public BusinessException addDetail(String key, Object value) {
        this.details.put(key, value);
        return this;
    }
}
