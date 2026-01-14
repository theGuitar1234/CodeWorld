package az.codeworld.springboot.security.dtos;

import java.time.Instant;

import org.springframework.stereotype.Component;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Component
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AuditDTO {
    private Instant createdAtAuditInstant;
    private String createdByAuditString;
    private Instant lastModifiedAtAuditInstant;
    private String lastModifiedByAuditString;
}