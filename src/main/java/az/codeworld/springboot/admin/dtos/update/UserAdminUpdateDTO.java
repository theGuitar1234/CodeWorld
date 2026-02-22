package az.codeworld.springboot.admin.dtos.update;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
