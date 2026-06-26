package com.abhisheksingh.loanaxisapi.feature.loan.repository;

import com.abhisheksingh.loanaxisapi.feature.loan.entity.Loan;
import com.abhisheksingh.loanaxisapi.feature.loan.enums.LoanStatusType;
import com.abhisheksingh.loanaxisapi.feature.loan.enums.LoanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByCustomerIdAndStatus(Long customerId, LoanStatusType status);
    Optional<Loan> findByLoanType(LoanType loanType);
    List<Loan> findByCustomerId(Long customerId);

}
