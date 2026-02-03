package az.codeworld.springboot.admin.records;

import java.time.Instant;

public record ActivityRecord (
    String title,
    String description,
    String activityColor,
    Instant ocurredAt
){}
