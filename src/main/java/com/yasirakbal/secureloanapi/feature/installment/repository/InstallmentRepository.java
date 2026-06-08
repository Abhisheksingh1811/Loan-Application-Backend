package com.yasirakbal.secureloanapi.feature.installment.repository;

import com.yasirakbal.secureloanapi.feature.installment.entity.Installment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstallmentRepository extends JpaRepository<Installment, Long> {
    Optional<Installment> findByLoanIdAndInstallmentNumber(Long loanId, int installmentNumber);
}
