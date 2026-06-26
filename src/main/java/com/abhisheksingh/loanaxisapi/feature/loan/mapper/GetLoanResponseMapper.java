package com.abhisheksingh.loanaxisapi.feature.loan.mapper;

import com.abhisheksingh.loanaxisapi.common.mapper.BaseMapper;
import com.abhisheksingh.loanaxisapi.feature.installment.entity.Installment;
import com.abhisheksingh.loanaxisapi.feature.loan.dto.GetLoanResponse;
import com.abhisheksingh.loanaxisapi.feature.loan.dto.GetLoanResponseInstallmentsView;
import com.abhisheksingh.loanaxisapi.feature.loan.entity.Loan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GetLoanResponseMapper extends BaseMapper<Loan, GetLoanResponse> {
    GetLoanResponseInstallmentsView toInstallmentView(Installment installment);
}
