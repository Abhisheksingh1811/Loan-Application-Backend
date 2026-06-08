package com.yasirakbal.secureloanapi.feature.loan.exception;

import com.yasirakbal.secureloanapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class PreviousInstallmentNotPaidException extends BusinessException {
    public PreviousInstallmentNotPaidException() {
        super("Previous installment hasn't been paid", HttpStatus.BAD_REQUEST);
    }

    public PreviousInstallmentNotPaidException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}