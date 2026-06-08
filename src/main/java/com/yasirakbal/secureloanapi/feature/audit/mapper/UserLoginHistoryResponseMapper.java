package com.yasirakbal.secureloanapi.feature.audit.mapper;

import com.yasirakbal.secureloanapi.common.mapper.BaseMapper;
import com.yasirakbal.secureloanapi.feature.audit.dto.UserLoginHistoryResponse;
import com.yasirakbal.secureloanapi.feature.audit.entity.LoginHistory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserLoginHistoryResponseMapper extends BaseMapper<LoginHistory, UserLoginHistoryResponse> {
}
