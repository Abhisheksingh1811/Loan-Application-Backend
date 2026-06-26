package com.abhisheksingh.loanaxisapi.feature.admin.dto;

import com.abhisheksingh.loanaxisapi.common.dto.PaginationResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public class GetAuditLogsPaginationResponse extends PaginationResponse<GetAuditLogsResponse> {
    public GetAuditLogsPaginationResponse(Page page, List<GetAuditLogsResponse> content) {
        super(page, content);
    }
}
