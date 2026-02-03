package az.codeworld.springboot.admin.dtos.create;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;

import az.codeworld.springboot.utilities.constants.currency;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class TransactionCreateDTO {

    @NotNull
    private BigDecimal transactionAmount;

    @NotNull
    private BigDecimal transactionFee;

    // @NotNull
    // private BigDecimal transactionTotal;

    @NotNull
    private currency currency;

    private String transactionDescription;
    private String paidBy;
}
