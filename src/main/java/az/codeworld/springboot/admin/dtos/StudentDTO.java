package az.codeworld.springboot.admin.dtos;

import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.entities.Money;
import az.codeworld.springboot.aop.validations.EmailValidation;
import az.codeworld.springboot.security.dtos.AuditDTO;
import az.codeworld.springboot.utilities.constants.roles;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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
