package com.yasirakbal.secureloanapi.feature.auth.exception;

import com.yasirakbal.secureloanapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends BusinessException {
    public InvalidRefreshTokenException() {
        super("Given refresh token is invalid", HttpStatus.UNAUTHORIZED);
    }
}
