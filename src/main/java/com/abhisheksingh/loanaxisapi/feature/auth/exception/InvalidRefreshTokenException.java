package com.abhisheksingh.loanaxisapi.feature.auth.exception;

import com.abhisheksingh.loanaxisapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends BusinessException {
    public InvalidRefreshTokenException() {
        super("Given refresh token is invalid", HttpStatus.UNAUTHORIZED);
    }
}
