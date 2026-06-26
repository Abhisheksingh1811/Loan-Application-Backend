package com.abhisheksingh.loanaxisapi.feature.admin.mapper;

import com.abhisheksingh.loanaxisapi.common.mapper.BaseMapper;
import com.abhisheksingh.loanaxisapi.feature.admin.dto.GetAuditLogUserView;
import com.abhisheksingh.loanaxisapi.feature.admin.dto.GetAuditLogsResponse;
import com.abhisheksingh.loanaxisapi.feature.audit.entity.SecurityAuditLog;
import com.abhisheksingh.loanaxisapi.feature.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GetAuditLogsResponseMapper extends BaseMapper<SecurityAuditLog, GetAuditLogsResponse> {
    GetAuditLogUserView mapToUserView(User user);
}
