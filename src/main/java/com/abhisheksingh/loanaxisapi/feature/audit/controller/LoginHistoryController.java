package com.abhisheksingh.loanaxisapi.feature.audit.controller;

import com.abhisheksingh.loanaxisapi.feature.audit.dto.UserLoginHistoryResponse;
import com.abhisheksingh.loanaxisapi.feature.audit.entity.LoginHistory;
import com.abhisheksingh.loanaxisapi.feature.audit.mapper.UserLoginHistoryResponseMapper;
import com.abhisheksingh.loanaxisapi.feature.audit.service.LoginHistoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping(path="/api/audit")
@AllArgsConstructor
public class LoginHistoryController {
    private LoginHistoryService loginHistoryService;
    private UserLoginHistoryResponseMapper userLoginHistoryResponseMapper;

    @GetMapping("/login-history")
    public ResponseEntity<List<UserLoginHistoryResponse>> getUserLoginHistory(@AuthenticationPrincipal Jwt jwt) {
        long userId = jwt.getClaim("userId");
        List<LoginHistory> loginHistoryList = loginHistoryService.getUserLoginsHistory(userId);

        List<UserLoginHistoryResponse> responses = loginHistoryList.stream()
                .map((loginHistory) -> userLoginHistoryResponseMapper.map(loginHistory))
                .toList();

        return ResponseEntity.ok(responses);
    }
}
