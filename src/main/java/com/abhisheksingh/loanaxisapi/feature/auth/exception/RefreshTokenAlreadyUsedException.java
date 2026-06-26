package com.abhisheksingh.loanaxisapi.feature.auth.exception;

import com.abhisheksingh.loanaxisapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class RefreshTokenAlreadyUsedException extends BusinessException {
    public RefreshTokenAlreadyUsedException() {
        super("Given refresh token is already used", HttpStatus.UNAUTHORIZED);
    }

}
