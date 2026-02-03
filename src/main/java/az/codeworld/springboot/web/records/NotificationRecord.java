package az.codeworld.springboot.web.records;

import java.time.Instant;
import java.time.LocalDateTime;

public record NotificationRecord (
    Long id,
    String notificationContent,
    String createdAt,
    String link
) {}
