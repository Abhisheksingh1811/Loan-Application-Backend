package com.abhisheksingh.loanaxisapi.feature.application.repository;

import com.abhisheksingh.loanaxisapi.feature.application.entity.LoanApplication;
import com.abhisheksingh.loanaxisapi.feature.application.enums.LoanApplicationStatus;
import com.abhisheksingh.loanaxisapi.feature.user.enums.District;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    List<LoanApplication> findByCustomerIdAndStatus(Long customerId, LoanApplicationStatus status);
    List<LoanApplication> findByCustomerId(Long customerId);

    Page<LoanApplication> findAllByStatus(LoanApplicationStatus status, Pageable pageable);
    Page<LoanApplication> findAllByCustomerDistrict(
            District district,
            Pageable pageable
    );

    Page<LoanApplication> findAllByCustomerDistrictAndStatus(
            District district,
            LoanApplicationStatus status,
            Pageable pageable
    );
}
