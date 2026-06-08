package com.yasirakbal.secureloanapi.feature.admin.dto;

import lombok.Data;

@Data
public class GetAuditLogUserView {
    private String username;
    private String email;
}
