package com.yasirakbal.secureloanapi.feature.application.exception;

import com.yasirakbal.secureloanapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class CustomerAgeRequestedTermNotEligibleException extends BusinessException {
    public CustomerAgeRequestedTermNotEligibleException(int maxAge, int ageWhenWillBeFinished) {
        super(("Customer age must equal to or smaller than %d when " +
                "credit will be finished, the calculated age was %d").formatted(maxAge, ageWhenWillBeFinished),
                HttpStatus.BAD_REQUEST);
    }
}
