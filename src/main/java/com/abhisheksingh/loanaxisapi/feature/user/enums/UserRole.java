package com.abhisheksingh.loanaxisapi.feature.user.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    CUSTOMER("ROLE_CUSTOMER"), CREDIT_OFFICER("ROLE_CREDIT_OFFICER"), ADMIN("ROLE_ADMIN");

    private final String value;
}
