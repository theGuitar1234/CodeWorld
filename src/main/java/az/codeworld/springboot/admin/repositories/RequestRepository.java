package az.codeworld.springboot.admin.repositories;

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

    Optional<Request> findByRequestToken(String requestToken);

    List<Request> findTop10ByOrderByExpiresAtDesc();

    Optional<Request> findById(Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Request r WHERE r.id = :id")
    void deleteRequestById(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Request r WHERE r.expiresAt < :cutoff")
    int deleteExpiredRequests(@Param("cutoff") long cutoff);

}
