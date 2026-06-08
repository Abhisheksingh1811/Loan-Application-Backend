package com.yasirakbal.secureloanapi.feature.application.mapper;

import com.yasirakbal.secureloanapi.common.mapper.BaseMapper;
import com.yasirakbal.secureloanapi.feature.application.dto.GetCustomersApplicationsResponse;
import com.yasirakbal.secureloanapi.feature.application.entity.LoanApplication;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GetCustomersApplicationsResponseMapper extends BaseMapper<LoanApplication, GetCustomersApplicationsResponse> {
}
