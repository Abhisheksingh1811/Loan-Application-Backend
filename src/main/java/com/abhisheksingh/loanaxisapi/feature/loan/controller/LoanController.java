package com.abhisheksingh.loanaxisapi.feature.loan.controller;

import com.abhisheksingh.loanaxisapi.feature.loan.dto.GetLoanResponse;
import com.abhisheksingh.loanaxisapi.feature.loan.dto.GetMyLoansResponse;
import com.abhisheksingh.loanaxisapi.feature.loan.dto.PayInstallmentRequest;
import com.abhisheksingh.loanaxisapi.feature.loan.dto.PayInstallmentResponse;
import com.abhisheksingh.loanaxisapi.feature.loan.entity.Loan;
import com.abhisheksingh.loanaxisapi.feature.loan.mapper.GetLoanResponseMapper;
import com.abhisheksingh.loanaxisapi.feature.loan.mapper.GetMyLoansResponseMapper;
import com.abhisheksingh.loanaxisapi.feature.loan.service.LoanService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@AllArgsConstructor
@Validated
public class LoanController {
    private LoanService loanService;
    private GetMyLoansResponseMapper getMyLoansResponseMapper;
    private GetLoanResponseMapper getLoanResponseMapper;

    @GetMapping("/my")
    public ResponseEntity<List<GetMyLoansResponse>> getCustomersLoans(@AuthenticationPrincipal Jwt jwt) {
        List<Loan> loanList = loanService.getCustomersLoans(jwt.getClaim("userId"));
        List<GetMyLoansResponse> responseList = loanList.stream().map(loan -> getMyLoansResponseMapper.map(loan)).toList();
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/my/{id}")
    @PreAuthorize("@loanSecurityService.canAccessLoan(#id, #authentication)")
    public ResponseEntity<GetLoanResponse> getLoan(@PathVariable @Positive Long id, Authentication authentication) {
        Loan loan = loanService.getLoanById(id);
        GetLoanResponse response = getLoanResponseMapper.map(loan);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{loanId}/installments/{installmentId}/pay")
    @PreAuthorize("@loanSecurityService.cayPayLoan(#loanId, #authentication)")
    public ResponseEntity<PayInstallmentResponse> payInstallment(
            @PathVariable @Positive Long loanId,
            @PathVariable @Positive Long installmentId,
            @RequestBody @Valid PayInstallmentRequest request,
            Authentication authentication
            )
    {
        PayInstallmentResponse response = loanService.payInstallment(loanId, installmentId, request);

        return ResponseEntity.ok(response);
    }
}
