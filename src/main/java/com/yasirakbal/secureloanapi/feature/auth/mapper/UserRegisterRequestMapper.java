package com.yasirakbal.secureloanapi.feature.auth.mapper;

import com.yasirakbal.secureloanapi.common.mapper.BaseMapper;
import com.yasirakbal.secureloanapi.feature.auth.dto.UserRegisterRequest;
import com.yasirakbal.secureloanapi.feature.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRegisterRequestMapper extends BaseMapper<UserRegisterRequest, User> {
}
