package az.codeworld.springboot.admin.dtos.auth;

import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;

import az.codeworld.springboot.aop.validations.UsernameValidation;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Component
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    @NotNull
    @NotBlank
    @Length(min = 13, max = 13)
    //@Pattern(regexp = "^[STA]-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{1}$")
    @UsernameValidation
    private String username;
}
