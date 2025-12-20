package az.codeworld.springboot.admin.services;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.dtos.transactions.TransactionDTO;
import az.codeworld.springboot.admin.entities.Transaction;
import az.codeworld.springboot.utilities.constants.roles;

@Component
public interface TransactionService {
    
    void defaultMethod();

    void saveTransaction(Transaction transaction);
    void addTransactionsToUser(String username, Set<Long> transactionIds);

    List<TransactionDTO> getAllTransactions(roles role);
    List<TransactionDTO> getRecentTransactions(roles role);

    Transaction getTransactionByTransactionId(Long transactionId);

    Page<TransactionDTO> getPaginatedTransactions(roles role, int pageIndex, int pageSize, String sortBy, Direction direction);

}
