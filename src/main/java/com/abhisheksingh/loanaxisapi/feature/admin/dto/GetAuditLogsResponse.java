package com.abhisheksingh.loanaxisapi.feature.admin.dto;

import com.abhisheksingh.loanaxisapi.feature.audit.enums.AuditEventType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetAuditLogsResponse {
    private Long id;
    private AuditEventType eventType;
    private GetAuditLogUserView user;
    private String resource;
    private String failureReason;
    private String details;
    private boolean success;
    private LocalDateTime occurredAt;
}
