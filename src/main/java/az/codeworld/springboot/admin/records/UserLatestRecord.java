package az.codeworld.springboot.admin.records;

import java.time.Instant;
import java.time.LocalDateTime;

public record UserLatestRecord (
    String email,
    Instant createdAt,
    String profilePhoto
) {}
