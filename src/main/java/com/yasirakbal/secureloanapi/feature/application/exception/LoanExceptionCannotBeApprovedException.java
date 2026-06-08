package com.yasirakbal.secureloanapi.feature.application.exception;

import com.yasirakbal.secureloanapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class LoanExceptionCannotBeApprovedException extends BusinessException {
    public LoanExceptionCannotBeApprovedException() {
        super("Only the pending applications can be approved", HttpStatus.BAD_REQUEST);
    }
}
