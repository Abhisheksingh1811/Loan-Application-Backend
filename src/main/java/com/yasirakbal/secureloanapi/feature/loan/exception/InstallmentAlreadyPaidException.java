package com.yasirakbal.secureloanapi.feature.loan.exception;

import com.yasirakbal.secureloanapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InstallmentAlreadyPaidException extends BusinessException {
    public InstallmentAlreadyPaidException() {
        super("Installment has already been paid", HttpStatus.BAD_REQUEST);
    }

    public InstallmentAlreadyPaidException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}