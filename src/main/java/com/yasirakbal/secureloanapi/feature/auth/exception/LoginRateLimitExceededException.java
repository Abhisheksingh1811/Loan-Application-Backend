package com.yasirakbal.secureloanapi.feature.auth.exception;

import com.yasirakbal.secureloanapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class LoginRateLimitExceededException extends BusinessException {

    public LoginRateLimitExceededException(long retryAfterSeconds) {
        super(
                "Too many login attempts. Please try again after " + retryAfterSeconds + " seconds.",
                HttpStatus.TOO_MANY_REQUESTS
        );

        addDetail("retryAfterSeconds", retryAfterSeconds);
    }
}