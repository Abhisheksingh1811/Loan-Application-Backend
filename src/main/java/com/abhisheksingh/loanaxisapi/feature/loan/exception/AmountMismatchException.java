package com.abhisheksingh.loanaxisapi.feature.loan.exception;

import com.abhisheksingh.loanaxisapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class AmountMismatchException extends BusinessException {
    public AmountMismatchException() {
        super("The amounts do not match", HttpStatus.BAD_REQUEST);
    }

    public AmountMismatchException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
