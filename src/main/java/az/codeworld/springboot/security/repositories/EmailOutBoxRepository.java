package az.codeworld.springboot.security.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.security.entities.EmailOutbox;
import az.codeworld.springboot.utilities.constants.emailstatus;

@Repository
public interface EmailOutBoxRepository extends JpaRepository<EmailOutbox, Long> {
    Optional<EmailOutbox> findByOutBoxId(Long outBoxId);

    List<EmailOutbox> findTop50ByStatusOrderByCreatedAtAsc(emailstatus status);
}
