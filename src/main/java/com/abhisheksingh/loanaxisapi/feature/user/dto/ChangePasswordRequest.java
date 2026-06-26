package com.abhisheksingh.loanaxisapi.feature.user.dto;

import com.abhisheksingh.loanaxisapi.feature.auth.annotation.interfaces.PasswordMatches;
import com.abhisheksingh.loanaxisapi.feature.auth.annotation.interfaces.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@PasswordMatches
public class ChangePasswordRequest {
    @NotBlank
    @ValidPassword
    private String password;

    @NotBlank
    @ValidPassword
    private String matchingPassword;

    @NotBlank
    private String oldPassword;
}
