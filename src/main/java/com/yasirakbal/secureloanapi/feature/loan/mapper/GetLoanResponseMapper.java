package com.yasirakbal.secureloanapi.feature.loan.mapper;

import com.yasirakbal.secureloanapi.common.mapper.BaseMapper;
import com.yasirakbal.secureloanapi.feature.installment.entity.Installment;
import com.yasirakbal.secureloanapi.feature.loan.dto.GetLoanResponse;
import com.yasirakbal.secureloanapi.feature.loan.dto.GetLoanResponseInstallmentsView;
import com.yasirakbal.secureloanapi.feature.loan.entity.Loan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GetLoanResponseMapper extends BaseMapper<Loan, GetLoanResponse> {
    GetLoanResponseInstallmentsView toInstallmentView(Installment installment);
}
