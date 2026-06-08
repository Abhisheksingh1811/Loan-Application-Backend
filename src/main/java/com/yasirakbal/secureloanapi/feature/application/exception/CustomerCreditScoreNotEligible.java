package com.yasirakbal.secureloanapi.feature.application.exception;

import com.yasirakbal.secureloanapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;


public class CustomerCreditScoreNotEligible extends BusinessException {
    public CustomerCreditScoreNotEligible(int threshold) {
        super("Customer credit score must be bigger than %d".formatted(threshold),
                HttpStatus.BAD_REQUEST);
    }
}
