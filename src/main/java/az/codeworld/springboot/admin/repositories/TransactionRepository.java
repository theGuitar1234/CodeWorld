package az.codeworld.springboot.admin.repositories;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.admin.entities.Transaction;
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.utilities.constants.transactionstatus;
import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
                SELECT t FROM Transaction t
                JOIN t.user u
                WHERE u.id = :id
                AND t.transactionTime >= :startTime 
                AND t.transactionTime < :endTime
            """)
    Page<Transaction> findByUser_Id(Instant startTime, Instant endTime, @Param("id") Long id, Pageable pageable);

    @Query("""
                SELECT t FROM Transaction t
                WHERE t.belongsTo = :belongsTo
                AND t.transactionTime BETWEEN :startTime AND :endTime
            """)
    Page<Transaction> findByBelongsTo(Instant startTime, Instant endTime, roles belongsTo, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.belongsTo = :belongsTo")
    List<Transaction> findAllTransactionsByBelongsTo(roles belongsTo);

    List<Transaction> findTop10ByBelongsToOrderByTransactionTimeDesc(roles belongsTo);

    List<Transaction> findTop10ByUserIdOrderByTransactionTimeDesc(Long userId);

    List<Transaction> findTop15ByBelongsToOrderByTransactionTimeAsc(roles role);

}
