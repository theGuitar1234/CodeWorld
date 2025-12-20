package az.codeworld.springboot.admin.dtos;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.utilities.constants.roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {
    private Long requestId;
    private String firstName;
    private String lastName;
    private String email;
    private roles role;
    private String requestToken;
}
