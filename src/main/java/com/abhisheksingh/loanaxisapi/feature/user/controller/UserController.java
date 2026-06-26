package com.abhisheksingh.loanaxisapi.feature.user.controller;

import com.abhisheksingh.loanaxisapi.feature.user.dto.ChangePasswordRequest;
import com.abhisheksingh.loanaxisapi.feature.user.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path="/api/users")
@Validated
public class UserController {
    private UserService userService;

    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request, @AuthenticationPrincipal Jwt jwt) {
        userService.changePassword(request, jwt);

        return ResponseEntity.noContent().build();
    }

}
