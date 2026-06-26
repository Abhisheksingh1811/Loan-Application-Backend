package com.abhisheksingh.loanaxisapi.feature.officer.controller;

import com.abhisheksingh.loanaxisapi.feature.application.entity.LoanApplication;
import com.abhisheksingh.loanaxisapi.feature.application.enums.LoanApplicationStatus;
import com.abhisheksingh.loanaxisapi.feature.application.service.LoanApplicationService;
import com.abhisheksingh.loanaxisapi.feature.loan.entity.Loan;
import com.abhisheksingh.loanaxisapi.feature.officer.dto.ApproveLoanApplicationResponse;
import com.abhisheksingh.loanaxisapi.feature.officer.dto.GetLoanApplicationsPaginationResponse;
import com.abhisheksingh.loanaxisapi.feature.officer.dto.GetLoanApplicationsResponse;
import com.abhisheksingh.loanaxisapi.feature.officer.mapper.ApproveLoanAppResponseMapper;
import com.abhisheksingh.loanaxisapi.feature.officer.mapper.GetLoanApplicationsResponseMapper;
import com.abhisheksingh.loanaxisapi.feature.officer.service.CreditOfficerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/api/officer")
@AllArgsConstructor
@Validated
public class CreditOfficerController {
    private final CreditOfficerService creditOfficerService;
    private LoanApplicationService loanApplicationService;
    private GetLoanApplicationsResponseMapper getLoanApplicationsResponseMapper;
    private ApproveLoanAppResponseMapper approveLoanAppResponseMapper;

    @GetMapping("/applications")
    public ResponseEntity<GetLoanApplicationsPaginationResponse> getApplications(
            @RequestParam(required = false) LoanApplicationStatus status,
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        Long officerId = jwt.getClaim("userId");
        Page<LoanApplication> loanApplicationPage = loanApplicationService.getAllLoanApplicationsForOfficer(officerId,status, page, size, sortBy, direction);
        List<GetLoanApplicationsResponse> responses = loanApplicationPage.stream()
                .map(l -> getLoanApplicationsResponseMapper.map(l)).toList();
        GetLoanApplicationsPaginationResponse paginationResponse = new GetLoanApplicationsPaginationResponse(loanApplicationPage, responses);

        return ResponseEntity.ok(paginationResponse);
    }

    @PutMapping("/applications/{id}/approve")
    public ResponseEntity<ApproveLoanApplicationResponse> approveLoanApplication(@PathVariable @Positive Long id,@AuthenticationPrincipal Jwt jwt) {
        Long officerId = jwt.getClaim("userId");
        Loan createdLoan = creditOfficerService.approveLoanApplication(id,officerId);
        ApproveLoanApplicationResponse response = approveLoanAppResponseMapper.map(createdLoan);

         return ResponseEntity.ok(response);
    }

    @DeleteMapping("/applications/{id}/reject")
    public ResponseEntity<Void> rejectLoanApplication(@PathVariable @Positive Long id, @Valid @RequestBody @NotBlank String rejectionReason,@AuthenticationPrincipal Jwt jwt) {
        Long officerId = jwt.getClaim("userId");
        creditOfficerService.rejectLoanApplication(id,officerId, rejectionReason);

        return ResponseEntity.noContent().build();
    }
}
