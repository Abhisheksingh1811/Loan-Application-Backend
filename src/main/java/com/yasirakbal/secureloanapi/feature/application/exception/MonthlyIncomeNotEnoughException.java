package com.yasirakbal.secureloanapi.feature.application.exception;

import com.yasirakbal.secureloanapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class MonthlyIncomeNotEnoughException extends BusinessException {
    public MonthlyIncomeNotEnoughException() {
        super("The monthly income is not enough.", HttpStatus.BAD_REQUEST);
    }
}
