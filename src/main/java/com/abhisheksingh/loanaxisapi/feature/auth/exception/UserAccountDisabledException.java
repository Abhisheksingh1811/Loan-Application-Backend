package com.abhisheksingh.loanaxisapi.feature.auth.exception;

import com.abhisheksingh.loanaxisapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UserAccountDisabledException extends BusinessException {
    public UserAccountDisabledException() {
        super("Account is disabled.", HttpStatus.FORBIDDEN);
    }
}
