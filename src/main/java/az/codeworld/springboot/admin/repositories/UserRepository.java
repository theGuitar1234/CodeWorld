package az.codeworld.springboot.admin.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.admin.entities.User;
import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long userId);

    @Modifying
    @Transactional
    void deleteById(Long userId);

    @Modifying
    @Transactional
    void deleteByEmail(String email);

    @Modifying
    @Transactional
    void deleteByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.createdAt = :createdAt ORDER BY u.createdAt DESC")
    List<User> findNewestUsers(@Param("createdAt") LocalDateTime createdAt);

    List<User> findAllByEmail(String email);
    boolean existsByEmail(String email);
}
