package com.yasirakbal.secureloanapi.feature.auth.exception;

import com.yasirakbal.secureloanapi.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UserAccountDisabledException extends BusinessException {
    public UserAccountDisabledException() {
        super("Account is disabled.", HttpStatus.FORBIDDEN);
    }
}
