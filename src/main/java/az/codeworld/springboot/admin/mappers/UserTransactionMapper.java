package az.codeworld.springboot.admin.mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.dtos.transactions.UserTransactionDTO;
import az.codeworld.springboot.web.entities.Transaction;

@Component
public class UserTransactionMapper {

    public static UserTransactionDTO toUserTransactionDTO(
        List<Transaction> transactions
    ) {
        return UserTransactionDTO
            .builder()
            .transactions(transactions)
            .build();
    }
}
