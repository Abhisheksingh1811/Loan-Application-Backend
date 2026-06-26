package com.abhisheksingh.loanaxisapi.feature.application.exception;

import com.abhisheksingh.loanaxisapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

public class DtiNotEligibleException extends BusinessException {
    public DtiNotEligibleException(BigDecimal threshold, BigDecimal calculated) {
        super("The calculated dti must be smaller than %.2f, calculated dti was %.2f".formatted(threshold.doubleValue(), calculated.doubleValue()),
                HttpStatus.BAD_REQUEST);
    }
}
