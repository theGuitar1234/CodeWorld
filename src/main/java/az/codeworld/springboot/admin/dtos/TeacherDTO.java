package az.codeworld.springboot.admin.dtos;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.aop.validations.EmailValidation;
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.web.dtos.SubjectDTO;
import az.codeworld.springboot.web.entities.CourseOffering;
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
public class TeacherDTO {
    private Long id;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{1}[a-z]{1,14}$")
    private String firstName;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{1}[a-z]{1,20}$")
    private String lastName;

    @EmailValidation
    private String email;

    private boolean isBanned;

    private String affiliatedSince;

    private roles role;

    private BigDecimal amount;
}
