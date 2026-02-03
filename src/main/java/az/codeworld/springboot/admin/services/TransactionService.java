package az.codeworld.springboot.admin.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.dtos.ChartPoint;
import az.codeworld.springboot.admin.dtos.create.TransactionCreateDTO;
import az.codeworld.springboot.admin.dtos.transactions.TransactionDTO;
import az.codeworld.springboot.admin.entities.Transaction;
import az.codeworld.springboot.utilities.constants.roles;

public interface TransactionService {
    
    void defaultMethod();

    void saveTransaction(Transaction transaction);
    void addTransactionsToUser(String username, Set<Long> transactionIds);

    List<TransactionDTO> getAllTransactions(roles role);

    List<TransactionDTO> getRecentTransactions(roles role);
    List<TransactionDTO> getRecentTransactions(Long userId);

    Transaction getTransactionByTransactionId(Long transactionId);

    Page<TransactionDTO> getPaginatedTransactions(Instant startDate, Instant endDate, Long userId, int pageIndex, int pageSize, String sortBy, Direction direction);
    Page<TransactionDTO> getPaginatedTransactions(Instant startDate, Instant endDate, roles role, int pageIndex, int pageSize, String sortBy, Direction direction);

    List<ChartPoint> getChartPoints();

    void recordTransaction(Long teacherId, TransactionCreateDTO transactionCreateDTO);

}
