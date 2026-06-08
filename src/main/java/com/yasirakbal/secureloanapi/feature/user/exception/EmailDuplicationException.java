package com.yasirakbal.secureloanapi.feature.user.exception;

import com.yasirakbal.secureloanapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class EmailDuplicationException extends BusinessException {
    public EmailDuplicationException(String email) {
        super("Customer with email: %s already exists".formatted(email), HttpStatus.CONFLICT);
    }
}
