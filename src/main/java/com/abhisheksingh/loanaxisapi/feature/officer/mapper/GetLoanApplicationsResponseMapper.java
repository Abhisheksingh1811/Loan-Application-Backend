package com.abhisheksingh.loanaxisapi.feature.officer.mapper;

import com.abhisheksingh.loanaxisapi.common.mapper.BaseMapper;
import com.abhisheksingh.loanaxisapi.feature.application.entity.LoanApplication;
import com.abhisheksingh.loanaxisapi.feature.officer.dto.GetLoanAppCustomerResponseView;
import com.abhisheksingh.loanaxisapi.feature.officer.dto.GetLoanApplicationsResponse;
import com.abhisheksingh.loanaxisapi.feature.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GetLoanApplicationsResponseMapper extends BaseMapper<LoanApplication, GetLoanApplicationsResponse> {
    GetLoanAppCustomerResponseView toGetLoanAppCustomerResponseView(User customer);
}
