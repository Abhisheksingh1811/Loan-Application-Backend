package com.abhisheksingh.loanaxisapi.feature.loan.entity;

import com.abhisheksingh.loanaxisapi.common.entity.BaseEntity;
import com.abhisheksingh.loanaxisapi.feature.installment.entity.Installment;
import com.abhisheksingh.loanaxisapi.feature.loan.enums.LoanStatusType;
import com.abhisheksingh.loanaxisapi.feature.loan.enums.LoanType;
import com.abhisheksingh.loanaxisapi.feature.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loans", indexes = {
        @Index(name = "idx_loans_customer_id", columnList = "customer_id"),
        @Index(name = "idx_loans_status", columnList = "status"),
        @Index(name = "idx_loans_next_due_date", columnList = "next_due_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan extends BaseEntity {
    @Column(nullable = false)
    private Long applicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private LoanType loanType;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Installment> installments = new ArrayList<>();

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal principalAmount;

    @Column(nullable = false, precision = 4, scale = 4)
    private BigDecimal interestRate;

    @Column(nullable = false)
    private Integer termMonths;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyInstallment;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private LoanStatusType status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal remainingBalance;

    @Column(nullable = false)
    @Builder.Default
    private Integer paidInstallments = 0;

    @Column(nullable = false)
    private LocalDate nextDueDate;

    @Column(nullable = false)
    private LocalDate disbursementDate;

    @Column(nullable = false)
    private LocalDate maturityDate;
}
