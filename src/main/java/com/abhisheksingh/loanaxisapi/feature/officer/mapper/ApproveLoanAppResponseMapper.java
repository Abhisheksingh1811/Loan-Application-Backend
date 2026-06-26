package com.abhisheksingh.loanaxisapi.feature.officer.mapper;

import com.abhisheksingh.loanaxisapi.common.mapper.BaseMapper;
import com.abhisheksingh.loanaxisapi.feature.loan.entity.Loan;
import com.abhisheksingh.loanaxisapi.feature.officer.dto.ApproveLoanApplicationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApproveLoanAppResponseMapper extends BaseMapper<Loan, ApproveLoanApplicationResponse> {
    @Override
    @Mapping(target = "loanId", source = "id")
    ApproveLoanApplicationResponse map(Loan laon);
}
