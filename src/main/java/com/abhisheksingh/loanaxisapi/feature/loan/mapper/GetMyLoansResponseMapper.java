package com.abhisheksingh.loanaxisapi.feature.loan.mapper;

import com.abhisheksingh.loanaxisapi.common.mapper.BaseMapper;
import com.abhisheksingh.loanaxisapi.feature.loan.dto.GetMyLoansResponse;
import com.abhisheksingh.loanaxisapi.feature.loan.entity.Loan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GetMyLoansResponseMapper extends BaseMapper<Loan, GetMyLoansResponse> {
}
