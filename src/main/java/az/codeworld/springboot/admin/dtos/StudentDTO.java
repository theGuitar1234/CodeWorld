package az.codeworld.springboot.admin.dtos;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.aop.validations.EmailValidation;
import az.codeworld.springboot.utilities.constants.roles;
import jakarta.validation.constraints.NotBlank;
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
public class StudentDTO {
    private Long id;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{1}[a-z]{1,14}$")
    private String firstName;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{1}[a-z]{1,20}$")
    private String lastName;

    @EmailValidation
    private String email;

    private String affiliatedSince;

    private boolean isBanned;

    private roles role;
}
