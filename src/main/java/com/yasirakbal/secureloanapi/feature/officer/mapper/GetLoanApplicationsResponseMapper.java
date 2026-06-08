package com.yasirakbal.secureloanapi.feature.officer.mapper;

import com.yasirakbal.secureloanapi.common.mapper.BaseMapper;
import com.yasirakbal.secureloanapi.feature.application.entity.LoanApplication;
import com.yasirakbal.secureloanapi.feature.officer.dto.GetLoanAppCustomerResponseView;
import com.yasirakbal.secureloanapi.feature.officer.dto.GetLoanApplicationsResponse;
import com.yasirakbal.secureloanapi.feature.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GetLoanApplicationsResponseMapper extends BaseMapper<LoanApplication, GetLoanApplicationsResponse> {
    GetLoanAppCustomerResponseView toGetLoanAppCustomerResponseView(User customer);
}
