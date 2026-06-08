package com.yasirakbal.secureloanapi.feature.admin.mapper;

import com.yasirakbal.secureloanapi.common.mapper.BaseMapper;
import com.yasirakbal.secureloanapi.feature.admin.dto.GetAuditLogUserView;
import com.yasirakbal.secureloanapi.feature.admin.dto.GetAuditLogsResponse;
import com.yasirakbal.secureloanapi.feature.audit.entity.SecurityAuditLog;
import com.yasirakbal.secureloanapi.feature.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GetAuditLogsResponseMapper extends BaseMapper<SecurityAuditLog, GetAuditLogsResponse> {
    GetAuditLogUserView mapToUserView(User user);
}
