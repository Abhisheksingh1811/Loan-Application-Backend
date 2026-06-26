package com.abhisheksingh.loanaxisapi.feature.auth.mapper;

import com.abhisheksingh.loanaxisapi.common.mapper.BaseMapper;
import com.abhisheksingh.loanaxisapi.feature.auth.dto.UserRegisterResponse;
import com.abhisheksingh.loanaxisapi.feature.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRegisterResponseMapper extends BaseMapper<User, UserRegisterResponse> {
}
