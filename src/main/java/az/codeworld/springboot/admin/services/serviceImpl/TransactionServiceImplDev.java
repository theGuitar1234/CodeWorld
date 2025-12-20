package az.codeworld.springboot.admin.services.serviceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.dtos.transactions.TransactionDTO;
import az.codeworld.springboot.admin.entities.Transaction;
import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.admin.mappers.TransactionMapper;
import az.codeworld.springboot.admin.repositories.TransactionRepository;
import az.codeworld.springboot.admin.repositories.UserRepository;
import az.codeworld.springboot.admin.services.TransactionService;
import az.codeworld.springboot.utilities.constants.roles;
import jakarta.transaction.Transactional;

@Service
@Profile("dev")
public class TransactionServiceImplDev implements TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionServiceImplDev(
        TransactionRepository transactionRepository,
        UserRepository userRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void defaultMethod() {}

    @Override
    public void saveTransaction(Transaction transaction) {
        if (transaction.getTransactionDate() == null) {
            transaction.setTransactionDate(LocalDate.now());
        }
        transactionRepository.save(transaction);
        transactionRepository.flush();
    }

    @Override
    public Transaction getTransactionByTransactionId(Long transactionId) {
        Optional<Transaction> transactionOptional = transactionRepository.findById(transactionId);
        Transaction transaction = transactionOptional.orElseThrow(() -> new RuntimeException("Transaction does not exist"));
        return transaction;
    }

    @Override
    @Transactional
    public void addTransactionsToUser(String username, Set<Long> transactionIds) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        User user = userOptional.orElseThrow(() -> new RuntimeException("User Not Foumd By Username"));

        for (Long transactionId : transactionIds) {
            Transaction transaction = getTransactionByTransactionId(transactionId);
            user.addTransaction(transaction);
        }
    }

    @Override
    public List<TransactionDTO> getAllTransactions(roles role) {
        return transactionRepository
            .findTop10ByRoleOrderByTransactionDateDesc(role)
            .stream()
            .map(transaction -> {
                return TransactionMapper.toTransactionDTO(
                    transaction.getTransactionId(),
                    transaction.getTransactionDate(),
                    transaction.getTransactionPaidBy(),
                    transaction.getTransactionDescription(),
                    transaction.getTransactionDetails(),
                    transaction.getTransactionAmount(),
                    transaction.getTransactionFee(),
                    transaction.getTransactionTotal(),
                    transaction.getStatus(),
                    transaction.getCurrency()
                );
            })
            .toList();
    }  

    @Override
    public List<TransactionDTO> getRecentTransactions(roles role) {
        return transactionRepository   
            .findTop10ByRoleOrderByTransactionDateDesc(role)
            .stream()
            .map(transaction -> {
                return TransactionMapper.toTransactionDTO(
                    transaction.getTransactionId(),
                    transaction.getTransactionDate(),
                    transaction.getTransactionPaidBy(),
                    transaction.getTransactionDescription(),
                    transaction.getTransactionDetails(),
                    transaction.getTransactionAmount(),
                    transaction.getTransactionFee(),
                    transaction.getTransactionTotal(),
                    transaction.getStatus(),
                    transaction.getCurrency()
                );
            })
            .toList();
    }

    @Override
    public Page<TransactionDTO> getPaginatedTransactions(
        roles role, 
        int pageIndex, 
        int pageSize, 
        String sortBy,
        Direction direction
    ) {
        return transactionRepository
            .findByRole(
                role,
                PageRequest.of(pageIndex, pageSize).withSort(direction, sortBy)
            )
            .map(t -> {
                return TransactionMapper.toTransactionDTO(
                    t.getTransactionId(), 
                    t.getTransactionDate(), 
                    t.getTransactionPaidBy(), 
                    t.getTransactionDescription(), 
                    t.getTransactionDetails(), 
                    t.getTransactionAmount(), 
                    t.getTransactionFee(), 
                    t.getTransactionTotal(), 
                    t.getStatus(), 
                    t.getCurrency()
                );
            });
    }
}
