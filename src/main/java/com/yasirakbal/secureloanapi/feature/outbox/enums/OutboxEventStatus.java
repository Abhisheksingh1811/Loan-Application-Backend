package com.yasirakbal.secureloanapi.feature.outbox.enums;

public enum OutboxEventStatus {
    PENDING,
    PROCESSING,
    SENT,
    FAILED
}