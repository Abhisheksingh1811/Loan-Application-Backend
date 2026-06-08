package com.yasirakbal.secureloanapi.feature.auth.mapper;

import com.yasirakbal.secureloanapi.common.mapper.BaseMapper;
import com.yasirakbal.secureloanapi.feature.auth.dto.UserRegisterResponse;
import com.yasirakbal.secureloanapi.feature.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRegisterResponseMapper extends BaseMapper<User, UserRegisterResponse> {
}
