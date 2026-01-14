package az.codeworld.springboot.admin.dtos.transactions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.entities.Money;
import az.codeworld.springboot.admin.entities.Transaction;
import az.codeworld.springboot.utilities.constants.currency;
import az.codeworld.springboot.utilities.constants.transactionstatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@Component
@ToString
@NoArgsConstructor
public class TransactionDTO {
    private Long transactionId;
    private Object transactionDate;
    private String transactionPaidBy;
    private String transactionDescription;
    private String transactionDetails;
    private BigDecimal transactionAmount;
    private BigDecimal transactionFee;
    private BigDecimal transactionTotal;
    private transactionstatus status;
    private currency currency;

    public TransactionDTO(
        Long transactionId,
        Object transactionDate,
        String transactionPaidBy,
        String transactionDescription,
        String transactionDetails,
        BigDecimal transactionAmount,
        BigDecimal transactionFee,
        BigDecimal transactionTotal,
        transactionstatus status,
        currency currency
    ) {
        this.transactionId = transactionId;
        this.transactionDate = transactionDate.toString();
        this.transactionPaidBy = transactionPaidBy;
        this.transactionDescription =  transactionDescription;
        this.transactionDetails =  transactionDetails;
        this.transactionAmount =  transactionAmount;
        this.transactionFee =  transactionFee;
        this.transactionTotal =  transactionTotal;
        this.status =  status;
        this.currency =  currency;
    }
}
