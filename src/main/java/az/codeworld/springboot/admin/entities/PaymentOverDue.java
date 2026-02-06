package az.codeworld.springboot.admin.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

import az.codeworld.springboot.utilities.constants.paymentDueStatus;
import az.codeworld.springboot.utilities.constants.transactionstatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PAYMENT_OVER_DUES")
public class PaymentOverDue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @Column(unique = true, nullable = false)
    private int cycleYear;

    @Column(unique = true, nullable = false)
    private int cycleMonth;

    @Column(nullable = false)
    private Instant dueDate;

    @Size(min = 6)
    @Column(nullable = false)
    private BigDecimal amount;

    @Column
    @Enumerated(EnumType.STRING)
    private paymentDueStatus paymentDueStatus;

    @Column
    private Instant createdAt;

    @Column
    private Instant paidAt;
}
