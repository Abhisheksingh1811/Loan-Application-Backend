package com.abhisheksingh.loanaxisapi.feature.user.exception;

import com.abhisheksingh.loanaxisapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class EmailDuplicationException extends BusinessException {
    public EmailDuplicationException(String email) {
        super("Customer with email: %s already exists".formatted(email), HttpStatus.CONFLICT);
    }
}
