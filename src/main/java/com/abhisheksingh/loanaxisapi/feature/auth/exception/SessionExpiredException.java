package com.abhisheksingh.loanaxisapi.feature.auth.exception;

import com.abhisheksingh.loanaxisapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class SessionExpiredException extends BusinessException {
    public SessionExpiredException() {
        super("Session expired. Please login again.", HttpStatus.UNAUTHORIZED);
    }
}
