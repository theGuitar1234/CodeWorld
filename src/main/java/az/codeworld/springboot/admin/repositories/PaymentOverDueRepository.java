package az.codeworld.springboot.admin.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.admin.entities.PaymentOverDue;
import az.codeworld.springboot.utilities.constants.paymentDueStatus;

@Repository
public interface PaymentOverDueRepository extends JpaRepository<PaymentOverDue, Long> {
    
    // @Modifying(clearAutomatically = true, flushAutomatically = true)
    // @Transactional
    // @Query(value = """
    //     UPDATE payment_due
    //     SET status = 'PAID', paid_at = NOW()
    //     WHERE id = :id AND status = 'DUE'
    //     """, nativeQuery = true)
    // int markPaidIfDue(@Param("id") long id);

    List<PaymentOverDue> findTop10ByPaymentDueStatusOrderByDueDateAsc(paymentDueStatus paymentDueStatus);
}

