package com.yasirakbal.secureloanapi.security.exception;

import com.yasirakbal.secureloanapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class AccessTokenRequiredException extends BusinessException {
    public AccessTokenRequiredException() {
        super("Access token is required.", HttpStatus.UNAUTHORIZED);
    }
}
