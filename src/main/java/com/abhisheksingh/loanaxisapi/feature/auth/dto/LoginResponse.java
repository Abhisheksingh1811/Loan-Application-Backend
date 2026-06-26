package com.abhisheksingh.loanaxisapi.feature.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String accessToken;

    private String refreshToken;

    private String tokenType;

    private Integer expiresIn;

    private LoginUserResponse user;

    @Data
    @Builder
    public static class LoginUserResponse {
        private Long id;

        private String username;

        private String email;

        private String fullName;

        private String role;
    }
}
