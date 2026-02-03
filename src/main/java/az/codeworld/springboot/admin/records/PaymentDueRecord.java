package az.codeworld.springboot.admin.records;

import java.math.BigDecimal;

public record PaymentDueRecord(
    Long id,
    Long teacherId,
    int cycleYear,
    int cycleMongth,
    String dueDate,
    BigDecimal amount,
    String status,
    String createdAt,
    String paidAt
) {}
