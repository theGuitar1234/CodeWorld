package az.codeworld.springboot.admin.services;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;

import az.codeworld.springboot.admin.dtos.ChartPoint;
import az.codeworld.springboot.admin.dtos.create.TransactionCreateDTO;
import az.codeworld.springboot.admin.dtos.transactions.TransactionDTO;
import az.codeworld.springboot.admin.entities.Transaction;
import az.codeworld.springboot.utilities.constants.roles;

public interface TransactionService {
    
    void defaultMethod();

    void saveTransaction(Transaction transaction);
    void addTransactionsToUser(String username, Set<Long> transactionIds);

    List<TransactionDTO> getRecentTransactions(roles role);
    List<TransactionDTO> getRecentTransactions(Long userId);

    Transaction getTransactionByTransactionId(Long transactionId);

    Page<TransactionDTO> getPaginatedTransactions(Instant startDate, Instant endDate, Long userId, int pageIndex, int pageSize, String sortBy, Direction direction);
    Page<TransactionDTO> getPaginatedTransactions(Instant startDate, Instant endDate, roles role, int pageIndex, int pageSize, String sortBy, Direction direction);

    void recordTransaction(Long teacherId, TransactionCreateDTO transactionCreateDTO);

    List<TransactionDTO> getAllTransactions(int pageIndex, int pageSize, String sortBy, Direction direction,
            roles role);

    List<ChartPoint> getTransactionsOverTime(roles role);

}
