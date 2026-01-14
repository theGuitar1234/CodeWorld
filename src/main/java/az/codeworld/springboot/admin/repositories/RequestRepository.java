package az.codeworld.springboot.admin.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.admin.entities.Request;
import jakarta.transaction.Transactional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT r FROM Request r")
    List<Optional<Request>> findAllRequests();

    Optional<Request> findByRequestToken(String requestToken);

    @Query("SELECT r FROM Request r ORDER BY r.expiresAt ASC LIMIT 10")
    List<Optional<Request>> findRecentRequests();

    Optional<Request> findByRequestId(Long requestId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Request r WHERE r.requestId = :requestId")
    void deleteRequestByRequestId(@Param("requestId") Long requestId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Request r WHERE r.expiresAt < :cutoff")
    int deleteExpiredRequests(@Param("cutoff") long cutoff);

}
