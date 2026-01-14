package az.codeworld.springboot.admin.mappers;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.dtos.transactions.TransactionDTO;
import az.codeworld.springboot.admin.entities.Money;
import az.codeworld.springboot.utilities.constants.currency;
import az.codeworld.springboot.utilities.constants.transactionstatus;

@Component
public class TransactionMapper {
    public static TransactionDTO toTransactionDTO(
        Long transactionId,
        LocalDate transactionDate,
        String transactionPaidBy,
        String transactionDescription,
        String transactionDetails,
        BigDecimal transactionAmount,
        BigDecimal transactionFee,
        BigDecimal transactionTotal,
        transactionstatus status,
        currency currency
    ) {
        return TransactionDTO
            .builder()
            .transactionId(transactionId)
            .transactionDate(transactionDate)
            .transactionPaidBy(transactionPaidBy)
            .transactionDescription(transactionDescription)
            .transactionDetails(transactionDetails)
            .transactionAmount(transactionAmount)
            .transactionFee(transactionFee)
            .transactionTotal(transactionTotal)
            .status(status)
            .currency(currency)
            .build();
    }
}
