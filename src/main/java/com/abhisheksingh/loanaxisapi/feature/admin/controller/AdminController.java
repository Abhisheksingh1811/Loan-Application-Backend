package com.abhisheksingh.loanaxisapi.feature.admin.controller;

import com.abhisheksingh.loanaxisapi.feature.admin.dto.GetAuditLogsPaginationResponse;
import com.abhisheksingh.loanaxisapi.feature.admin.mapper.GetAuditLogsResponseMapper;
import com.abhisheksingh.loanaxisapi.feature.admin.service.AdminService;
import com.abhisheksingh.loanaxisapi.feature.audit.entity.SecurityAuditLog;
import com.abhisheksingh.loanaxisapi.feature.audit.service.AuditLogService;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import com.abhisheksingh.loanaxisapi.feature.admin.dto.GetAdminUsersPaginationResponse;
import com.abhisheksingh.loanaxisapi.feature.admin.dto.GetAdminUsersResponse;
import com.abhisheksingh.loanaxisapi.feature.user.enums.District;
import com.abhisheksingh.loanaxisapi.feature.user.enums.UserRole;
@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
@Validated
public class AdminController {
    private AdminService adminService;
    private AuditLogService auditLogService;
    private GetAuditLogsResponseMapper getAuditLogsResponseMapper;

    @GetMapping("/security/audit-logs")
    public ResponseEntity<GetAuditLogsPaginationResponse> getAuditLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String resource,
            @RequestParam(required = false) String httpMethod,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) String userAgent,
            @RequestParam(required = false) Boolean success,
            @RequestParam(required = false) LocalDateTime timestampFrom,
            @RequestParam(required = false) LocalDateTime timestampTo,

            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction)
    {

        Page<SecurityAuditLog> auditLogPage = auditLogService.getAllAuditLogs(
                userId, action, resource, httpMethod, ipAddress, userAgent, success,
                timestampFrom, timestampTo, page, size, sortBy, direction
        );

        var auditLogResponses = auditLogPage.stream().map(log -> getAuditLogsResponseMapper.map(log)).toList();
        var paginationResponse = new GetAuditLogsPaginationResponse(auditLogPage, auditLogResponses);

        return ResponseEntity.ok(paginationResponse);
    }

    @PostMapping("/users/{id}/lock")
    public ResponseEntity<Void> lockUser(@PathVariable @Positive Long id) {
        adminService.lockUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{id}/unlock")
    public ResponseEntity<Void> unlockUser(@PathVariable @Positive Long id) {
        adminService.unlockUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{id}/force-logout")
    public ResponseEntity<Void> forceLogoutUser(@PathVariable @Positive Long id) {
        adminService.forceLogoutUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public ResponseEntity<GetAdminUsersPaginationResponse> getAdminUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) District district,
            @RequestParam(required = false) Boolean accountLocked,

            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        Page<GetAdminUsersResponse> usersPage = adminService.getAdminUsers(
                search,
                role,
                district,
                accountLocked,
                page,
                size,
                sortBy,
                direction
        );

        return ResponseEntity.ok(
                new GetAdminUsersPaginationResponse(
                        usersPage,
                        usersPage.getContent()
                )
        );
    }
}
