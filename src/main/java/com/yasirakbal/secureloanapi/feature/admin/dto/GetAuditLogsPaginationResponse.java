package com.yasirakbal.secureloanapi.feature.admin.dto;

import com.yasirakbal.secureloanapi.common.dto.PaginationResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public class GetAuditLogsPaginationResponse extends PaginationResponse<GetAuditLogsResponse> {
    public GetAuditLogsPaginationResponse(Page page, List<GetAuditLogsResponse> content) {
        super(page, content);
    }
}
