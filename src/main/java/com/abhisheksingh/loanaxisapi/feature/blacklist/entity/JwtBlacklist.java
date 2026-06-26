package com.abhisheksingh.loanaxisapi.feature.blacklist.entity;

import com.abhisheksingh.loanaxisapi.common.entity.BaseEntity;
import com.abhisheksingh.loanaxisapi.feature.blacklist.enums.JwtBlacklistReason;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "jwt_blacklists", indexes = {
        @Index(name = "idx_jwt_blacklists_token", columnList = "token"),
        @Index(name = "idx_jwt_blacklists_user_id", columnList = "user_id"),
        @Index(name = "idx_jwt_blacklists_expires_at", columnList = "expires_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtBlacklist extends BaseEntity {
    @Column(length = 750, nullable = false, unique = true)
    private String token;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private JwtBlacklistReason reason;

    @Column(nullable = false)
    private LocalDateTime blacklistedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;
}
