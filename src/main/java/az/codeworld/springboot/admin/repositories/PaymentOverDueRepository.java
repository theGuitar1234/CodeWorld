package az.codeworld.springboot.admin.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.admin.entities.PaymentOverDue;

@Repository
public interface PaymentOverDueRepository extends JpaRepository<PaymentOverDue, Long> {
    
}
