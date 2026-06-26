package com.abhisheksingh.loanaxisapi.feature.auth.exception;

import com.abhisheksingh.loanaxisapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ExpiredRefreshTokenException extends BusinessException {
    public ExpiredRefreshTokenException() {
        super("Given refresh token is expired", HttpStatus.UNAUTHORIZED);
    }
}
