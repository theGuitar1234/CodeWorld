package az.codeworld.springboot.admin.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.admin.entities.Transaction;
import az.codeworld.springboot.utilities.constants.roles;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Page<Transaction> findByBelongsTo(roles belongsTo, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.belongsTo = :belongsTo")
    List<Transaction> findAllTransactionsByBelongsTo(roles belongsTo);

    List<Transaction> findTop10ByBelongsToOrderByTransactionDateDesc(roles belongsTo);
}
