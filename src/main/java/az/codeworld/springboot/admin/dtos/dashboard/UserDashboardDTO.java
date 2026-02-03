package az.codeworld.springboot.admin.dtos.dashboard;

import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.entities.Money;
import az.codeworld.springboot.security.entities.Role;
import az.codeworld.springboot.utilities.constants.roles;
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

    private Long id;

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

    @Builder.Default
    private String profilePhoto = "/assets/sprites/profile-thumb.jpg";

    private String passwordLastUpdatedAt;

    public boolean isPastPayment;
    public boolean isNearPayment;

    private roles role;

}
