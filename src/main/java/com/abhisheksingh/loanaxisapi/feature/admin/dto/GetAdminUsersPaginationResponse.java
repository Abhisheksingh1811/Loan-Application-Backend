package com.abhisheksingh.loanaxisapi.feature.admin.dto;

import com.abhisheksingh.loanaxisapi.common.dto.PaginationResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public class GetAdminUsersPaginationResponse extends PaginationResponse<GetAdminUsersResponse> {
    public GetAdminUsersPaginationResponse(Page page, List<GetAdminUsersResponse> content) {
        super(page, content);
    }
}
