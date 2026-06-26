package com.abhisheksingh.loanaxisapi.feature.installment.service;

import com.abhisheksingh.loanaxisapi.feature.installment.entity.Installment;
import com.abhisheksingh.loanaxisapi.feature.installment.repository.InstallmentRepository;
import com.abhisheksingh.loanaxisapi.feature.loan.entity.Loan;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class InstallmentService {
    private InstallmentRepository installmentRepository;

    public List<Installment> calculateInstallments(Loan loan) {
        int termMonths = loan.getTermMonths();
        BigDecimal interestRate = loan.getInterestRate();
        BigDecimal monthlyInstallment = loan.getMonthlyInstallment();
        BigDecimal remainingBalance = loan.getPrincipalAmount();
        LocalDate currentDueDate = loan.getNextDueDate();

        List<Installment> installments = new ArrayList<>(termMonths);

        for (int i = 1; i <= termMonths; i++) {
            BigDecimal monthlyInterestAmount = remainingBalance
                    .multiply(interestRate)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal monthlyPrincipalAmount;
            BigDecimal installmentAmount;

            if (i == termMonths) {
                monthlyPrincipalAmount = remainingBalance;
                installmentAmount = monthlyPrincipalAmount.add(monthlyInterestAmount);
            } else {
                monthlyPrincipalAmount = monthlyInstallment.subtract(monthlyInterestAmount);
                installmentAmount = monthlyInstallment;
            }

            Installment installment = Installment.builder()
                    .loan(loan)
                    .installmentNumber(i)
                    .dueDate(currentDueDate)
                    .amount(installmentAmount)
                    .principalAmount(monthlyPrincipalAmount)
                    .interestAmount(monthlyInterestAmount)
                    .build();

            installments.add(installment);

            remainingBalance = remainingBalance.subtract(monthlyPrincipalAmount);
            currentDueDate = currentDueDate.plusMonths(1);
        }

        return installments;
    }

    public BigDecimal calculateMonthlyInstallment(BigDecimal creditAmount, BigDecimal monthlyInterestRate, int term) {
        BigDecimal interestRatePowTerm = BigDecimal.ONE.add(monthlyInterestRate).pow(term);
        BigDecimal numerator = creditAmount.multiply(monthlyInterestRate).multiply(interestRatePowTerm);
        BigDecimal denominator = interestRatePowTerm.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }
}
