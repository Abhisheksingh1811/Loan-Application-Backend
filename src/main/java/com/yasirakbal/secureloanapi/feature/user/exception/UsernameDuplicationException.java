package com.yasirakbal.secureloanapi.feature.user.exception;

import com.yasirakbal.secureloanapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UsernameDuplicationException extends BusinessException {
    public UsernameDuplicationException(String username) {
        super("Customer with username: %s already exists".formatted(username), HttpStatus.CONFLICT);
    }
}
