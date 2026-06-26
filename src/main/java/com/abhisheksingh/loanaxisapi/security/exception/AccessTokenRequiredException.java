package com.abhisheksingh.loanaxisapi.security.exception;

import com.abhisheksingh.loanaxisapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class AccessTokenRequiredException extends BusinessException {
    public AccessTokenRequiredException() {
        super("Access token is required.", HttpStatus.UNAUTHORIZED);
    }
}
