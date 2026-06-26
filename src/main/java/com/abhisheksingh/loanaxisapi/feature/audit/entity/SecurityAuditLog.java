package com.abhisheksingh.loanaxisapi.feature.audit.entity;

import com.abhisheksingh.loanaxisapi.common.entity.BaseEntity;
import com.abhisheksingh.loanaxisapi.feature.audit.enums.AuditEventType;
import com.abhisheksingh.loanaxisapi.feature.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "security_audit_logs", indexes = {
        @Index(name = "idx_security_audit_logs_user_id", columnList = "user_id"),
        @Index(name = "idx_security_audit_logs_action", columnList = "event_type"),
        @Index(name = "idx_security_audit_logs_timestamp", columnList = "occurred_at"),
        @Index(name = "idx_security_audit_logs_success", columnList = "success")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityAuditLog extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    @Enumerated(EnumType.STRING)
    private AuditEventType eventType;

    @Column(length = 255)
    private String resource;

    @Column(nullable = false)
    private Boolean success;

    @Column(length = 255)
    private String failureReason;

    @Column
    private String details;

    @Column(nullable = false)
    private LocalDateTime occurredAt;
}
