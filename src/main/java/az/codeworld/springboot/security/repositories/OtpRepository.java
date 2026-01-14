package az.codeworld.springboot.security.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.security.entities.OtpCode;

// import jakarta.transaction.Transactional;

@Repository
public interface OtpRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findByEmailAndOtpCode(String email, String otpCode);
}
