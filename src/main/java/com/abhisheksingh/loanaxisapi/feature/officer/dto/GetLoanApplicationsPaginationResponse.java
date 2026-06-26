package com.abhisheksingh.loanaxisapi.feature.officer.dto;

import com.abhisheksingh.loanaxisapi.common.dto.PaginationResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public class GetLoanApplicationsPaginationResponse extends PaginationResponse<GetLoanApplicationsResponse> {
    public GetLoanApplicationsPaginationResponse(Page page, List<GetLoanApplicationsResponse> content) {
        super(page, content);
    }
}
