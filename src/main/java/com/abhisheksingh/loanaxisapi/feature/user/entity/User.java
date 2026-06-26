package com.abhisheksingh.loanaxisapi.feature.user.entity;

import com.abhisheksingh.loanaxisapi.common.entity.BaseEntity;
import com.abhisheksingh.loanaxisapi.feature.application.entity.LoanApplication;
import com.abhisheksingh.loanaxisapi.feature.audit.entity.LoginHistory;
import com.abhisheksingh.loanaxisapi.feature.audit.entity.SecurityAuditLog;
import com.abhisheksingh.loanaxisapi.feature.loan.entity.Loan;
import com.abhisheksingh.loanaxisapi.feature.user.enums.District;
import com.abhisheksingh.loanaxisapi.feature.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email"),
        @Index(name = "idx_users_identity_number", columnList = "identityNumber"),
        @Index(name = "idx_users_username", columnList = "username")
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {
    @Column(nullable = false, length = 50, unique = true)
    private String username;

    @Column(nullable = false, length = 255)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private String password;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 11, unique = true)
    private String identityNumber;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(nullable = false)
    private Integer creditScore;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private District district;

    //Relations

    @OneToMany(mappedBy = "customer")
    private List<LoanApplication> applications = new ArrayList<>();

    @OneToMany(mappedBy = "evaluator")
    private List<LoanApplication> evaluatedApplications = new ArrayList<>();

    @OneToMany(mappedBy = "customer")
    private List<Loan> loans = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<LoginHistory> loginHistories = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<SecurityAuditLog> auditLogs = new ArrayList<>();


    //Security Fields

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean accountLocked = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    @Column
    private LocalDateTime lockedUntil;

    @Column
    private LocalDateTime lastLoginAt;

    @Column
    private LocalDateTime tokensInvalidatedAt;

    @Column(length = 50)
    private String lastLoginIp;

    @Column
    private LocalDateTime passwordChangedAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean passwordExpired = false;
}
