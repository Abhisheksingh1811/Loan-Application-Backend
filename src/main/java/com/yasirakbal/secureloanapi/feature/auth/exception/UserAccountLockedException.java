package com.yasirakbal.secureloanapi.feature.auth.exception;

import com.yasirakbal.secureloanapi.common.exception.BusinessException;
import com.yasirakbal.secureloanapi.common.exception.NonRollbackBusinessException;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class UserAccountLockedException extends NonRollbackBusinessException {
    public UserAccountLockedException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public UserAccountLockedException(LocalDateTime lockedUntil) {
        super("Account is temporarily locked", HttpStatus.FORBIDDEN);
        if (lockedUntil != null) {
            long minutesRemaining = Duration.between(LocalDateTime.now(), lockedUntil).toMinutes();
            addDetail("lockedUntil", lockedUntil);
            addDetail("unlockInMinutes", minutesRemaining);
            addDetail("message", "Account will be unlocked in " + minutesRemaining + " minutes");
        }
    }
}