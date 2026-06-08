package com.yasirakbal.secureloanapi.feature.loan.mapper;

import com.yasirakbal.secureloanapi.common.mapper.BaseMapper;
import com.yasirakbal.secureloanapi.feature.loan.dto.GetMyLoansResponse;
import com.yasirakbal.secureloanapi.feature.loan.entity.Loan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GetMyLoansResponseMapper extends BaseMapper<Loan, GetMyLoansResponse> {
}
