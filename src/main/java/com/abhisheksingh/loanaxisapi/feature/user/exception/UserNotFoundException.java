package com.abhisheksingh.loanaxisapi.feature.user.exception;

import com.abhisheksingh.loanaxisapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(Long userId) {
        super("User with user id: %d, is not found".formatted(userId), HttpStatus.BAD_REQUEST);
    }
}
