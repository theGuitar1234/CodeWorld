package az.codeworld.springboot.admin.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.admin.entities.PaymentOverDue;
import az.codeworld.springboot.utilities.constants.paymentDueStatus;

@Repository
public interface PaymentOverDueRepository extends JpaRepository<PaymentOverDue, Long> {
    
    List<PaymentOverDue> findTop10ByPaymentDueStatusOrderByDueDateAsc(paymentDueStatus paymentDueStatus);
}

