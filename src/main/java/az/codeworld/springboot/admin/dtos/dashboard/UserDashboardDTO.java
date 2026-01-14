package az.codeworld.springboot.admin.dtos.dashboard;

import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.entities.Money;
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
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
public class UserDashboardDTO {

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

    private String nextDate;
    private Money payment;
    private boolean isEmailAdded;
    private boolean isPhoneAdded;
    private boolean isCardAdded;
    private boolean isBankAccountAdded;
    private byte completeness;
}
