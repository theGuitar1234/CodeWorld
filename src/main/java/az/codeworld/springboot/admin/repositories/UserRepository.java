package az.codeworld.springboot.admin.repositories;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.admin.entities.Transaction;
import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.admin.projections.UserAdminProjection;
import az.codeworld.springboot.admin.projections.UserLogoutProjection;
import az.codeworld.springboot.security.entities.Role;
import az.codeworld.springboot.utilities.constants.accountstatus;
import az.codeworld.springboot.utilities.constants.roles;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    Optional<User> findByUserName(String userName);
    Optional<User> findById(Long userId);

    @Modifying
    @Transactional
    void deleteById(Long userId);

    @Modifying
    @Transactional
    void deleteByEmail(String email);

    @Modifying
    @Transactional
    void deleteByUserName(String userName);

    @Query("SELECT u FROM User u WHERE u.createdAt = :createdAt ORDER BY u.createdAt DESC")
    List<User> findNewestUsers(@Param("createdAt") Instant createdAt);

    List<User> findAllByEmail(String email);
    boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query("""
        UPDATE User u 
        SET u.lastActiveAt = :now
        WHERE u.userName = :userName 
        AND (u.lastActiveAt IS NULL OR u.lastActiveAt < :cutoff)
    """)
    void updateLastActiveAtByUserName(
        @Param("userName") String userName, 
        @Param("now") Instant now, 
        @Param("cutoff") Instant cutoff
    );

    Page<User> findByRolesAndBillingEnabledTrueAndNextDateLessThanEqual(
        Role role,
        Instant nextDate,
        boolean billingEnabled, 
        Pageable pageable
    );

    <T> Optional<T> findById(Long id, Class<T> type);
    <T> Optional<T> findByUserName(String userName, Class<T> type);

    int countByAccountStatus(accountstatus accountStatus);
    int countByIsBanned(boolean isBanned);
    int countByCreatedAtAfter(Instant cutoff);
    int countByLastActiveAtBeforeOrLastActiveAtIsNull(Instant cutoff);
    int countByLastActiveAtAfter(Instant cutoff);
    
    @Query("SELECT COUNT(*) FROM User u")
    Long countAll();

    List<User> findTop10ByCreatedAtGreaterThanEqualOrderByCreatedAtDesc(Instant cutoff);

}
