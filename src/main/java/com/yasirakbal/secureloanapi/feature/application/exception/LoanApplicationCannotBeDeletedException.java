package com.yasirakbal.secureloanapi.feature.application.exception;

import com.yasirakbal.secureloanapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class LoanApplicationCannotBeDeletedException extends BusinessException {
    public LoanApplicationCannotBeDeletedException() {
        super("Only the pending applications can be deleted", HttpStatus.BAD_REQUEST);
    }
}
