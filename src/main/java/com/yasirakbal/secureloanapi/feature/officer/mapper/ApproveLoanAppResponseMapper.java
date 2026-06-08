package com.yasirakbal.secureloanapi.feature.officer.mapper;

import com.yasirakbal.secureloanapi.common.mapper.BaseMapper;
import com.yasirakbal.secureloanapi.feature.loan.entity.Loan;
import com.yasirakbal.secureloanapi.feature.officer.dto.ApproveLoanApplicationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApproveLoanAppResponseMapper extends BaseMapper<Loan, ApproveLoanApplicationResponse> {
    @Override
    @Mapping(target = "loanId", source = "id")
    ApproveLoanApplicationResponse map(Loan laon);
}
