package com.yasirakbal.secureloanapi.feature.user.controller;

import com.yasirakbal.secureloanapi.feature.blacklist.entity.JwtBlacklist;
import com.yasirakbal.secureloanapi.feature.blacklist.enums.JwtBlacklistReason;
import com.yasirakbal.secureloanapi.feature.blacklist.service.JwtBlacklistService;
import com.yasirakbal.secureloanapi.feature.user.dto.ChangePasswordRequest;
import com.yasirakbal.secureloanapi.feature.user.service.UserService;
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

import java.time.LocalDateTime;
import java.time.ZoneOffset;

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
