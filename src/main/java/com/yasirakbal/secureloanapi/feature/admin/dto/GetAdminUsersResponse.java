package com.yasirakbal.secureloanapi.feature.admin.dto;

import com.yasirakbal.secureloanapi.feature.user.enums.District;
import com.yasirakbal.secureloanapi.feature.user.enums.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetAdminUsersResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private UserRole role;
    private District district;
    private Boolean accountLocked;
    private Integer failedLoginAttempts;
    private LocalDateTime lockedUntil;
}