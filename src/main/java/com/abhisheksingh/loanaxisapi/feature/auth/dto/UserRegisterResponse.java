package com.abhisheksingh.loanaxisapi.feature.auth.dto;

import lombok.Data;

@Data
public class UserRegisterResponse {
    private Long id;

    private String username;

    private String email;

    private String fullName;

    private String role;
}
