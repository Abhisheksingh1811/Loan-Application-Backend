package com.abhisheksingh.loanaxisapi.feature.audit.mapper;

import com.abhisheksingh.loanaxisapi.common.mapper.BaseMapper;
import com.abhisheksingh.loanaxisapi.feature.audit.dto.UserLoginHistoryResponse;
import com.abhisheksingh.loanaxisapi.feature.audit.entity.LoginHistory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserLoginHistoryResponseMapper extends BaseMapper<LoginHistory, UserLoginHistoryResponse> {
}
