package az.codeworld.springboot.security.dtos;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.utilities.constants.roles;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    
    @Enumerated(EnumType.STRING)
    private roles role;
}
