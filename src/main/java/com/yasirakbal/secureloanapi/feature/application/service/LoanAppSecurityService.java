package com.yasirakbal.secureloanapi.feature.application.service;

import com.yasirakbal.secureloanapi.feature.application.entity.LoanApplication;
import com.yasirakbal.secureloanapi.feature.application.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanAppSecurityService {
    private final LoanApplicationRepository loanApplicationRepository;

    public boolean canAccessApplication(Long applicationId, Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");

        if(authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("SCOPE_ROLE_ADMIN") || a.getAuthority().equals("SCOPE_ROLE_CREDIT_OFFICER"))) {
            return true;
        }

        LoanApplication loanApplication = loanApplicationRepository.findById(applicationId).orElse(null);

        return loanApplication != null && loanApplication.getCustomer().getId().equals(userId);
    }


    public boolean canDeleteApplication(Long applicationId, Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");

        LoanApplication loanApplication = loanApplicationRepository.findById(applicationId).orElse(null);

        return loanApplication != null && loanApplication.getCustomer().getId().equals(userId);
    }
}
