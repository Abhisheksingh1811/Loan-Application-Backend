package com.yasirakbal.secureloanapi.feature.audit.entity;

import com.yasirakbal.secureloanapi.common.entity.BaseEntity;
import com.yasirakbal.secureloanapi.feature.audit.enums.AuditEventType;
import com.yasirakbal.secureloanapi.feature.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_histories", indexes = {
        @Index(name = "idx_login_histories_user_id", columnList = "user_id"),
        @Index(name = "idx_login_histories_timestamp", columnList = "timestamp"),
        @Index(name = "idx_login_histories_success", columnList = "success")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginHistory extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 100)
    private String attemptedUsername;

    @Column(length = 50)
    private String ipAddress;

    @Column(length = 255)
    private String userAgent;

    @Column(length = 50)
    private String browser;

    @Column(length = 50)
    private String os;

    @Column(length = 50)
    private String device;

    @Column(nullable = false)
    private Boolean success;

    @Column(length = 255)
    private String failureReason;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
