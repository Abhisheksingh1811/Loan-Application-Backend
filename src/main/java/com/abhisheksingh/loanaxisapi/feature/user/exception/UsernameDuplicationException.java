package com.abhisheksingh.loanaxisapi.feature.user.exception;

import com.abhisheksingh.loanaxisapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UsernameDuplicationException extends BusinessException {
    public UsernameDuplicationException(String username) {
        super("Customer with username: %s already exists".formatted(username), HttpStatus.CONFLICT);
    }
}
