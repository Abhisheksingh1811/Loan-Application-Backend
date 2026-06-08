package com.yasirakbal.secureloanapi.feature.application.controller;

import com.yasirakbal.secureloanapi.feature.application.dto.CreateLoanApplicationRequest;
import com.yasirakbal.secureloanapi.feature.application.dto.CreateLoanApplicationResponse;
import com.yasirakbal.secureloanapi.feature.application.dto.GetCustomersApplicationsResponse;
import com.yasirakbal.secureloanapi.feature.application.entity.LoanApplication;
import com.yasirakbal.secureloanapi.feature.application.enums.LoanApplicationStatus;
import com.yasirakbal.secureloanapi.feature.application.mapper.CreateLoanAppResponseMapper;
import com.yasirakbal.secureloanapi.feature.application.mapper.GetCustomersApplicationsResponseMapper;
import com.yasirakbal.secureloanapi.feature.application.service.LoanApplicationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path="/api/applications")
@AllArgsConstructor
@Validated
public class LoanApplicationController {
    private LoanApplicationService loanApplicationService;
    private CreateLoanAppResponseMapper createLoanAppResponseMapper;
    private GetCustomersApplicationsResponseMapper getCustomersApplicationsResponseMapper;

    @PostMapping
    public ResponseEntity<CreateLoanApplicationResponse> createLoanApplication(@RequestBody @Valid CreateLoanApplicationRequest request,
                                                                               @AuthenticationPrincipal Jwt jwt) {
        LoanApplication createdLoan = loanApplicationService.createApplication(request, jwt.getClaim("userId"));

        CreateLoanApplicationResponse response = createLoanAppResponseMapper.map(createdLoan);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<GetCustomersApplicationsResponse>> getCustomersApplications(@RequestParam("status") Optional<LoanApplicationStatus> status,
                                                                                           @AuthenticationPrincipal Jwt jwt) {
        List<LoanApplication> loanApplications = loanApplicationService.getCustomersApplications(jwt.getClaim("userId"), status.orElse(null));

        List<GetCustomersApplicationsResponse> responses = loanApplications.stream()
                .map(loanApp -> getCustomersApplicationsResponseMapper.map(loanApp))
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@loanAppSecurityService.canAccessApplication(#id, #authentication)")
    public ResponseEntity<GetCustomersApplicationsResponse> getApplication(@PathVariable @Positive Long id, Authentication authentication) {
        LoanApplication loanApplication =  loanApplicationService.getLoanApplicationById(id);
        GetCustomersApplicationsResponse response = getCustomersApplicationsResponseMapper.map(loanApplication);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@loanAppSecurityService.canDeleteApplication(#id, #authentication)")
    public ResponseEntity<Void> deleteApplication(@PathVariable @Positive Long id, Authentication authentication) {
        loanApplicationService.deleteLoanApplication(id);
        return ResponseEntity.noContent().build();
    }
}
