package com.abhisheksingh.loanaxisapi.feature.application.entity;

import com.abhisheksingh.loanaxisapi.common.entity.BaseEntity;
import com.abhisheksingh.loanaxisapi.feature.application.enums.LoanApplicationStatus;
import com.abhisheksingh.loanaxisapi.feature.loan.enums.LoanType;
import com.abhisheksingh.loanaxisapi.feature.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_applications", indexes = {
        @Index(name = "idx_loan_applications_customer_id", columnList = "customer_id"),
        @Index(name = "idx_loan_applications_status", columnList = "status"),
        @Index(name = "idx_loan_applications_evaluated_by", columnList = "evaluated_by")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplication extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private LoanType loanType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal requestedAmount;

    @Column(nullable = false)
    private Integer requestedTerm;

    @Column(length = 500)
    private String purpose;

    //Calculated fields
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyInstallment;

    @Column(nullable = false, precision = 4, scale = 4)
    private BigDecimal interestRate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPayment;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal dtiRatio;

    //Evaluation
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private LoanApplicationStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluated_by")
    private User evaluator;

    @Column
    private LocalDateTime evaluatedAt;

    @Column
    private String rejectionReasons;

}
