package az.codeworld.springboot.admin.dtos.update;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

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
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminUpdateDTO {
   private String password;
   private String password2;
   private BigDecimal payment;
   private LocalDate nextDate;
   private boolean banUnban;
   private LocalDate affiliationDate;
}
