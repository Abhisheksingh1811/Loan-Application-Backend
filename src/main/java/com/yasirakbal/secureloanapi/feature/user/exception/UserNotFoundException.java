package com.yasirakbal.secureloanapi.feature.user.exception;

import com.yasirakbal.secureloanapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(Long userId) {
        super("User with user id: %d, is not found".formatted(userId), HttpStatus.BAD_REQUEST);
    }
}
