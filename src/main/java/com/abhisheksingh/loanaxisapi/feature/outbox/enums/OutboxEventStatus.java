package com.abhisheksingh.loanaxisapi.feature.outbox.enums;

public enum OutboxEventStatus {
    PENDING,
    PROCESSING,
    SENT,
    FAILED,
    DEAD
}
