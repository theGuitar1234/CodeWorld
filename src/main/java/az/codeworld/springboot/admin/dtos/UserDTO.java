package az.codeworld.springboot.admin.dtos;

import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.entities.Money;
import az.codeworld.springboot.aop.validations.EmailValidation;
import az.codeworld.springboot.security.dtos.AuditDTO;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Component
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO extends AuditDTO {

    @NotNull
    @NotBlank
    @Length(min = 13, max = 13)
    @Pattern(regexp = "^[STA]-[A-Z0-9]{4}-[A-Z0-9]{4}-\\d{1,}$")
    private String userName;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{1}[a-z]{1,14}$")
    private String firstName;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{1}[a-z]{1,20}$")
    private String lastName;

    @EmailValidation
    private String email;

    private String createdAt;
    private Money payment;
    private String nextDate;

    private String birthDate;
    private String street;
    private String city;
    private String region;
    private int postalCode;
    private String country;
    private String language;
    private String timeZone;

    @Pattern(regexp = "^\\+\\d{3}\\d{2}\\d{3}\\d{2}\\d{2}$")
    private String phoneNumber;

    @Min(0)
    @Max(150)
    private Integer age;

    private String presence;

    private String profilePhoto;
}
