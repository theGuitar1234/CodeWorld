package az.codeworld.springboot.admin.dtos.create;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Component
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {
    private String firstname;
    private String lastname;
}
