package com.yasirakbal.secureloanapi.feature.auth.controller;

import com.yasirakbal.secureloanapi.feature.auth.dto.*;
import com.yasirakbal.secureloanapi.feature.auth.mapper.UserRegisterRequestMapper;
import com.yasirakbal.secureloanapi.feature.auth.mapper.UserRegisterResponseMapper;
import com.yasirakbal.secureloanapi.feature.auth.service.AuthService;
import com.yasirakbal.secureloanapi.feature.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path="/api/auth")
@AllArgsConstructor
@Validated
public class AuthController {
    private AuthService authService;
    private UserRegisterRequestMapper userRegisterRequestMapper;
    private UserRegisterResponseMapper userRegisterResponseMapper;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        User user = userRegisterRequestMapper.map(request);
        User registeredUser = authService.registerUser(user);

        UserRegisterResponse response = userRegisterResponseMapper.map(registeredUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        LoginResponse loginResponse = authService.login(request.getUsername(), request.getPassword(), httpRequest);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal Jwt jwt) {
        authService.logoutUser(jwt);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse refreshTokenResponse = authService.refreshToken(request.getRefreshToken());

        return ResponseEntity.ok(refreshTokenResponse);
    }
}
