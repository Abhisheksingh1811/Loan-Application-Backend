package com.yasirakbal.secureloanapi.feature.admin.dto;

import com.yasirakbal.secureloanapi.common.dto.PaginationResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public class GetAdminUsersPaginationResponse extends PaginationResponse<GetAdminUsersResponse> {
    public GetAdminUsersPaginationResponse(Page page, List<GetAdminUsersResponse> content) {
        super(page, content);
    }
}