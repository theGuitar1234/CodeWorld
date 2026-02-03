package az.codeworld.springboot.admin.mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.dtos.transactions.TransactionDTO;
import az.codeworld.springboot.admin.entities.Money;
import az.codeworld.springboot.admin.entities.Transaction;
import az.codeworld.springboot.utilities.constants.currency;
import az.codeworld.springboot.utilities.constants.transactionstatus;

@Component
public class TransactionMapper {

    private static final String ZONE = "Asia/Baku";
    private static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm";

    public static TransactionDTO toTransactionDTO(
        Transaction transaction
    ) {
        return TransactionDTO
            .builder()
            .transactionId(transaction.getTransactionId())
            .transactionTime(LocalDateTime.ofInstant(transaction.getTransactionTime(), ZoneId.of(ZONE)).format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)))
            .transactionPaidBy(transaction.getTransactionPaidBy())
            .transactionDescription(transaction.getTransactionDescription())
            .transactionDetails(transaction.getTransactionDetails())
            .transactionAmount(transaction.getTransactionAmount())
            .transactionFee(transaction.getTransactionFee())
            .transactionTotal(transaction.getTransactionTotal())
            .status(transaction.getStatus())
            .currency(transaction.getCurrency())
            .build();
    }
}
