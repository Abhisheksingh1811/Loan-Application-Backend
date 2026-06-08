package com.yasirakbal.secureloanapi.feature.loan.repository;

import com.yasirakbal.secureloanapi.feature.loan.entity.Loan;
import com.yasirakbal.secureloanapi.feature.loan.enums.LoanStatusType;
import com.yasirakbal.secureloanapi.feature.loan.enums.LoanType;
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
