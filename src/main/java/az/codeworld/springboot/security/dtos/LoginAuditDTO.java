package az.codeworld.springboot.security.dtos;

// import java.time.Instant;

import org.springframework.stereotype.Component;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Component
@NoArgsConstructor
public class LoginAuditDTO extends AuditDTO {

    private boolean isBlocked;
    private long blockExpiry;

    public LoginAuditDTO(
        boolean isBlocked,
        long blockExpiry
    ) {
        this.isBlocked = isBlocked;
        this.blockExpiry = blockExpiry;
    }
}