package az.codeworld.springboot.admin.entities;

import java.math.BigDecimal;
import java.time.Instant;

import az.codeworld.springboot.utilities.constants.paymentDueStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "PAYMENT_OVER_DUES",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_teacher_cycle",
            columnNames = { "teacher_id", "cycle_year", "cycle_month" }
        )
    },
    indexes = {
        @Index(name = "idx_pod_status_due", columnList = "payment_due_status,due_date"),
        @Index(name = "idx_pod_teacher", columnList = "teacher_id")
    }
)
public class PaymentOverDue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @Column(name = "cycle_year", nullable = false)
    private int cycleYear;

    @Column(name = "cycle_month", nullable = false)
    private int cycleMonth;

    @Column(name = "due_date", nullable = false)
    private Instant dueDate;

    @NotNull
    @DecimalMin("0.01")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_due_status", nullable = false)
    private paymentDueStatus status = paymentDueStatus.DUE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "paid_at")
    private Instant paidAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (status == null) status = paymentDueStatus.DUE;
    }
}