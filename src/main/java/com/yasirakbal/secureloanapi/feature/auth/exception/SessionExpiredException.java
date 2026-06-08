package com.yasirakbal.secureloanapi.feature.auth.exception;

import com.yasirakbal.secureloanapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class SessionExpiredException extends BusinessException {
    public SessionExpiredException() {
        super("Session expired. Please login again.", HttpStatus.UNAUTHORIZED);
    }
}
