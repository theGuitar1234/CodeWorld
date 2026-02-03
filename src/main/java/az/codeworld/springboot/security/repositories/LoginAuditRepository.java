package az.codeworld.springboot.security.repositories;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.security.entities.LoginAudit;

@Repository
public interface LoginAuditRepository extends JpaRepository<LoginAudit, Long> {
    int countByLastLoginAtIsBetween(Instant start, Instant end);
}
