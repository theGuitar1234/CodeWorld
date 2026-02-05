package az.codeworld.springboot.admin.records;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record PaymentOverDueRecord(
    Long id,
    Long teacherId,
    int cycleYear,
    int cycleMonth,
    String dueDate,
    BigDecimal amount,
    String status,
    String createdAt,
    String paidAt
) {}
