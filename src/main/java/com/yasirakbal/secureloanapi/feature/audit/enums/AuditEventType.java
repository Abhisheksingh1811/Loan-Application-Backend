package com.yasirakbal.secureloanapi.feature.audit.enums;

public enum AuditEventType {
    // Loan events
    LOAN_APPLICATION_CREATED,
    LOAN_APPROVED,
    LOAN_REJECTED,
    LOAN_DELETED,
    LOAN_CREATED,
    INSTALLMENT_PAID,

    // User events
    USER_CREATED,
    USER_LOCKED,
    USER_UNLOCKED,
    PASSWORD_CHANGED,

    // Security events
    TOKEN_BLACKLISTED,

    // Admin events
    ROLE_CHANGED,
    FORCE_LOGOUT
}