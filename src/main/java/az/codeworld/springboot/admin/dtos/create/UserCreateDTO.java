package az.codeworld.springboot.admin.dtos.create;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import az.codeworld.springboot.admin.entities.Money;
import az.codeworld.springboot.aop.validations.EmailValidation;
import az.codeworld.springboot.aop.validations.PasswordValidation;
import az.codeworld.springboot.utilities.constants.currency;
import az.codeworld.springboot.utilities.constants.roles;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {

    @Pattern(regexp = "^[A-Z]{1}[a-z]{1,14}$")
    private String firstName;

    @Pattern(regexp = "^[A-Z]{1}[a-z]{1,20}$")
    private String lastName;

    @NotNull
    @PasswordValidation
    private String password;

    @NotNull
    @PasswordValidation
    private String password2;

    private BigDecimal amount;

    @Past
    private LocalDate affiliationDate;

    private LocalDate nextDate;

    @EmailValidation
    private String email;

    @Enumerated(EnumType.STRING)
    private roles role;

    @Enumerated(EnumType.STRING)
    private currency currency;
}
