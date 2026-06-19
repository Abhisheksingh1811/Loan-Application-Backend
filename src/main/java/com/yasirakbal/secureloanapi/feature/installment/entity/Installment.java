package com.yasirakbal.secureloanapi.feature.installment.entity;

import com.yasirakbal.secureloanapi.common.entity.BaseEntity;
import com.yasirakbal.secureloanapi.feature.installment.enums.InstallmentStatus;
import com.yasirakbal.secureloanapi.feature.loan.entity.Loan;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "installments",
        indexes = {
                @Index(name = "idx_installments_loan_id", columnList = "loan_id"),
                @Index(name = "idx_installments_due_date", columnList = "due_date"),
                @Index(name = "idx_installments_status", columnList = "status")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_installments_loan_installment_number",
                        columnNames = {"loan_id", "installment_number"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Installment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id")
    private Loan loan;

    @Column(nullable = false)
    private Integer installmentNumber;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal principalAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal interestAmount;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private InstallmentStatus status = InstallmentStatus.PENDING;

    @Column
    private LocalDateTime paidDate;

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;
}
