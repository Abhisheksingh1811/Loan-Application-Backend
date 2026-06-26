package com.abhisheksingh.loanaxisapi.feature.auth.exception;

import com.abhisheksingh.loanaxisapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UserPasswordExpiredException extends BusinessException {
    public UserPasswordExpiredException() {
        super("User password is expired, please change your password.", HttpStatus.BAD_REQUEST);
    }
}
