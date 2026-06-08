package com.yasirakbal.secureloanapi.feature.admin.dto;

import com.yasirakbal.secureloanapi.feature.audit.enums.AuditEventType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
