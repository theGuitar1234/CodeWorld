package az.codeworld.springboot.admin.dtos.transactions;

import java.util.List;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.entities.Money;
import az.codeworld.springboot.admin.entities.Transaction;
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
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
public class UserPayableDTO {

    private Long id;
    
    @NotBlank
    @Pattern(regexp = "^[A-Z]{1}[a-z]{1,14}$")
    private String firstName;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{1}[a-z]{1,20}$")
    private String lastName;

    private Money payment;
    private String nextDate;

    private roles role;

    private String phoneNumber;
}
