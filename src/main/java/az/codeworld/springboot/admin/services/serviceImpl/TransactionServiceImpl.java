package az.codeworld.springboot.admin.services.serviceImpl;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.h2.mvstore.tx.TransactionMap;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.dtos.ChartPoint;
import az.codeworld.springboot.admin.dtos.create.TransactionCreateDTO;
import az.codeworld.springboot.admin.dtos.transactions.TransactionDTO;
import az.codeworld.springboot.admin.entities.Teacher;
import az.codeworld.springboot.admin.entities.Transaction;
import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.admin.mappers.TransactionMapper;
import az.codeworld.springboot.admin.repositories.TeacherRepository;
import az.codeworld.springboot.admin.repositories.TransactionRepository;
import az.codeworld.springboot.admin.repositories.UserRepository;
import az.codeworld.springboot.admin.services.TransactionService;
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.utilities.constants.transactionstatus;
import jakarta.transaction.Transactional;

@Service
public class TransactionServiceImpl implements TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final TransactionMapper transactionMapper;

    public TransactionServiceImpl(
        TransactionRepository transactionRepository,
        UserRepository userRepository,
        TeacherRepository teacherRepository,
        TransactionMapper transactionMapper
    ) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public void defaultMethod() {}

    @Override
    public void saveTransaction(Transaction transaction) {
        if (transaction.getTransactionTime() == null) {
            transaction.setTransactionTime(Instant.now());
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
        Optional<User> userOptional = userRepository.findByUserName(username);
        User user = userOptional.orElseThrow(() -> new RuntimeException("User Not Foumd By Username"));

        for (Long transactionId : transactionIds) {
            Transaction transaction = getTransactionByTransactionId(transactionId);
            user.addTransaction(transaction);
        }
    }

    @Override
    public List<TransactionDTO> getAllTransactions(roles role) {
        return transactionRepository
            .findAllTransactionsByBelongsTo(role)
            .stream()
            .map(transaction -> {
                return transactionMapper.toTransactionDTO(
                    transaction
                );
            })
            .toList();
    }  

    @Override
    public List<TransactionDTO> getRecentTransactions(roles role) {
        return transactionRepository   
            .findTop10ByBelongsToOrderByTransactionTimeDesc(role)
            .stream()
            .map(transaction -> {
                return transactionMapper.toTransactionDTO(
                    transaction
                );
            })
            .toList();
    }

    @Override
    public List<TransactionDTO> getRecentTransactions(Long userId) {
        return transactionRepository   
            .findTop10ByUserIdOrderByTransactionTimeDesc(userId)
            .stream()
            .map(transaction -> {
                return transactionMapper.toTransactionDTO(
                    transaction
                );
            })
            .toList();
    }

    @Override
    public Page<TransactionDTO> getPaginatedTransactions(
        Instant startTime,
        Instant endTime,
        Long userId,
        int pageIndex, 
        int pageSize, 
        String sortBy,
        Direction direction
    ) {
        return transactionRepository
            .findByUser_Id(
                startTime, 
                endTime,
                userId,
                PageRequest.of(pageIndex, pageSize).withSort(direction, sortBy)
            )
            .map(t -> {
                return transactionMapper.toTransactionDTO(
                    t
                );
            });
    }

    @Override
    public Page<TransactionDTO> getPaginatedTransactions(
        Instant startTime,
        Instant endTime,
        roles role, 
        int pageIndex, 
        int pageSize, 
        String sortBy,
        Direction direction
    ) {
        return transactionRepository
            .findByBelongsTo(
                startTime,
                endTime,
                role,
                PageRequest.of(pageIndex, pageSize).withSort(direction, sortBy)
            )
            .map(t -> {
                return transactionMapper.toTransactionDTO(
                    t
                );
            });
    }

    @Override
    public List<ChartPoint> getChartPoints() {
        return transactionRepository.findTop15ByBelongsToOrderByTransactionTimeAsc(roles.STUDENT)
            .stream()
            .map(t -> new ChartPoint(t.getTransactionTime().toEpochMilli(), t.getTransactionAmount()))
            .toList();
    }

    @Override
    @Transactional
    public void recordTransaction(Long userId, TransactionCreateDTO transactionCreateDTO) {
        Optional<User> userOptional = userRepository.findById(userId);
        User user = userOptional.orElseThrow(() -> new RuntimeException("User Not Found By ID"));
        
        if (
            transactionCreateDTO.getTransactionAmount() == null ||
            transactionCreateDTO.getTransactionAmount().signum() <= 0
        ) {
            throw new RuntimeException("Transaction Amount me be > 0");
        }

        if (
            transactionCreateDTO.getTransactionFee() == null ||
            transactionCreateDTO.getTransactionFee().signum() < 0
        ) {
            throw new RuntimeException("TransactionFee must be > 0");
        }

        BigDecimal transactionTotal = transactionCreateDTO.getTransactionAmount().add(transactionCreateDTO.getTransactionFee());

        Transaction transaction = new Transaction();
        transaction.setBelongsTo(user.getRoles().stream().anyMatch(r -> roles.TEACHER.getRoleNameString().equals(r.getRoleNameString())) ? roles.TEACHER : roles.STUDENT);
        transaction.setCurrency(transactionCreateDTO.getCurrency());
        transaction.setStatus(transactionstatus.CHECKED);
        transaction.setTransactionAmount(user.getPayment().getAmount());
        transaction.setTransactionFee(transactionCreateDTO.getTransactionFee());
        transaction.setTransactionDescription(transactionCreateDTO.getTransactionDescription());
        transaction.setTransactionPaidBy(transactionCreateDTO.getPaidBy());
        transaction.setTransactionTotal(transactionTotal);
        transaction.setUser(user);

        saveTransaction(transaction);
        
        user.addTransaction(transaction);
        user.updateNextPaymentDate();
        userRepository.save(user);
        userRepository.flush();
    }
}
