package com.abhisheksingh.loanaxisapi.feature.user.exception;

import com.abhisheksingh.loanaxisapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class IdentityNumberDuplicationException extends BusinessException {
    public IdentityNumberDuplicationException(String identityNumber) {
        super("Customer with identity number: %s already exists".formatted(identityNumber), HttpStatus.CONFLICT);
    }
}
