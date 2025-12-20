package az.codeworld.springboot.security.dtos;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.utilities.constants.roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@Component
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private roles role;
}
