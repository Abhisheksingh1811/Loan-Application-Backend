package com.abhisheksingh.loanaxisapi.feature.application.exception;

import com.abhisheksingh.loanaxisapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class LoanApplicationCannotBeDeletedException extends BusinessException {
    public LoanApplicationCannotBeDeletedException() {
        super("Only the pending applications can be deleted", HttpStatus.BAD_REQUEST);
    }
}
