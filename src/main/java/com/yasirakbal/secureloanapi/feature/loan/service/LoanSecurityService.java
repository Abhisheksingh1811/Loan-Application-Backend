package com.yasirakbal.secureloanapi.feature.loan.service;

import com.yasirakbal.secureloanapi.feature.application.entity.LoanApplication;
import com.yasirakbal.secureloanapi.feature.loan.entity.Loan;
import com.yasirakbal.secureloanapi.feature.loan.repository.LoanRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoanSecurityService {
    private LoanRepository loanRepository;

    public boolean canAccessLoan(Long loanId, Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");

        if(authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("SCOPE_ROLE_ADMIN") || a.getAuthority().equals("SCOPE_ROLE_CREDIT_OFFICER"))) {
            return true;
        }

        Loan loan = loanRepository.findById(loanId).orElse(null);

        return loan != null && loan.getCustomer().getId().equals(userId);
    }

    public boolean cayPayLoan(Long loanId, Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");

        Loan loan = loanRepository.findById(loanId).orElse(null);

        return loan != null && loan.getCustomer().getId().equals(userId);
    }
}
