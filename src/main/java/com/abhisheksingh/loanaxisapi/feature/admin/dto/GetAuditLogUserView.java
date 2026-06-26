package com.abhisheksingh.loanaxisapi.feature.admin.dto;

import lombok.Data;

@Data
public class GetAuditLogUserView {
    private Long id;
    private String username;
    private String email;
}
