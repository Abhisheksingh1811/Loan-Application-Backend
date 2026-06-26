package com.abhisheksingh.loanaxisapi.feature.audit.dto;

import com.abhisheksingh.loanaxisapi.feature.audit.enums.AuditEventType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserLoginHistoryResponse {
    private Long id;
    private String ipAddress;
    private String device;
    private String location;
    private String userAgent;
    private String browser;
    private String os;
    private Boolean success;
    private String failureReason;
    private LocalDateTime timestamp;
    private AuditEventType eventType;
}
