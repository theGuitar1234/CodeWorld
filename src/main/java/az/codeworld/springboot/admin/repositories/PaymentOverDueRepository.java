package az.codeworld.springboot.admin.repositories;

import java.util.List;
import java.util.Optional;

import az.codeworld.springboot.admin.entities.PaymentOverDue;
import az.codeworld.springboot.utilities.constants.paymentDueStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentOverDueRepository extends JpaRepository<PaymentOverDue, Long> {

    boolean existsByTeacher_IdAndCycleYearAndCycleMonth(Long teacherId, int cycleYear, int cycleMonth);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PaymentOverDue p join fetch p.teacher t where p.id = :id")
    Optional<PaymentOverDue> lockById(@Param("id") Long id);

    @Query("""
        select p from PaymentOverDue p
        join fetch p.teacher t
        where p.status = :status
        order by p.dueDate asc
    """)
    List<PaymentOverDue> findByStatusWithTeacher(@Param("status") paymentDueStatus status, Pageable pageable);
}