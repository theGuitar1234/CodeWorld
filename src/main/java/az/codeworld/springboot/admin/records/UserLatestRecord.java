package az.codeworld.springboot.admin.records;

import java.time.Instant;

public record UserLatestRecord (
    String email,
    Instant createdAt,
    String profilePhoto
) {}
