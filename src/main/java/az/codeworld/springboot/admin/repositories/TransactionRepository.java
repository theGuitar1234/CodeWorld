package az.codeworld.springboot.admin.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.admin.entities.Transaction;
import az.codeworld.springboot.utilities.constants.roles;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Page<Transaction> findByRole(roles role, Pageable pageable);

    List<Transaction> findTop10ByRoleOrderByTransactionDateDesc(roles role);
}
