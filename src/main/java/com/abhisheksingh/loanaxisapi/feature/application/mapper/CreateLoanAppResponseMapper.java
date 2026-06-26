package com.abhisheksingh.loanaxisapi.feature.application.mapper;

import com.abhisheksingh.loanaxisapi.common.mapper.BaseMapper;
import com.abhisheksingh.loanaxisapi.feature.application.dto.CreateLoanApplicationResponse;
import com.abhisheksingh.loanaxisapi.feature.application.entity.LoanApplication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CreateLoanAppResponseMapper extends BaseMapper<LoanApplication, CreateLoanApplicationResponse> {
    @Override
    @Mapping(
            source = "rejectionReasons",
            target = "rejectionReasons",
            qualifiedByName = "splitToList"
    )
    CreateLoanApplicationResponse map(LoanApplication source);

    @Named("splitToList")
    default List<String> splitToList(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split("; "))
                .map(String::trim)
                .toList();
    }
}
