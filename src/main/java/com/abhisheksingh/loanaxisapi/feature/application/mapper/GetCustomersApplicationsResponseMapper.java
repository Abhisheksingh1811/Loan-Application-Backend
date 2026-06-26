package com.abhisheksingh.loanaxisapi.feature.application.mapper;

import com.abhisheksingh.loanaxisapi.common.mapper.BaseMapper;
import com.abhisheksingh.loanaxisapi.feature.application.dto.GetCustomersApplicationsResponse;
import com.abhisheksingh.loanaxisapi.feature.application.entity.LoanApplication;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GetCustomersApplicationsResponseMapper extends BaseMapper<LoanApplication, GetCustomersApplicationsResponse> {
}
