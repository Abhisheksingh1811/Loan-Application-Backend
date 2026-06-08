package com.yasirakbal.secureloanapi.feature.officer.dto;

import com.yasirakbal.secureloanapi.common.dto.PaginationResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public class GetLoanApplicationsPaginationResponse extends PaginationResponse<GetLoanApplicationsResponse> {
    public GetLoanApplicationsPaginationResponse(Page page, List<GetLoanApplicationsResponse> content) {
        super(page, content);
    }
}
