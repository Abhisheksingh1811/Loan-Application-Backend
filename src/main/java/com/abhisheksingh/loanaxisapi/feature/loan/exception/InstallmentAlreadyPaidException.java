package com.abhisheksingh.loanaxisapi.feature.loan.exception;

import com.abhisheksingh.loanaxisapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InstallmentAlreadyPaidException extends BusinessException {
    public InstallmentAlreadyPaidException() {
        super("Installment has already been paid", HttpStatus.BAD_REQUEST);
    }

    public InstallmentAlreadyPaidException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
