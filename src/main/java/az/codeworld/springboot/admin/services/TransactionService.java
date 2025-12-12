package az.codeworld.springboot.admin.services;

import java.util.List;
import java.util.Set;

import az.codeworld.springboot.admin.entities.Transaction;

public interface TransactionService {
    
    void defaultMethod();

    void saveTransaction(Transaction transaction);
    void addTransactionsToUser(String username, Set<Long> transactionIds);
    
    Transaction getTransactionByTransactionId(Long transactionId);
}
