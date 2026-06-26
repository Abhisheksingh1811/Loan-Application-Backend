package com.abhisheksingh.loanaxisapi.feature.application.exception;

import com.abhisheksingh.loanaxisapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class MonthlyIncomeNotEnoughException extends BusinessException {
    public MonthlyIncomeNotEnoughException() {
        super("The monthly income is not enough.", HttpStatus.BAD_REQUEST);
    }
}
