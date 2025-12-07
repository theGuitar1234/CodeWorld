package az.codeworld.springboot.security.dtos;

import org.springframework.stereotype.Component;

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
    private String firstNameString;
    private String lastNameString;
    private String passwordString;
}
