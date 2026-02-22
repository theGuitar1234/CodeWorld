package az.codeworld.springboot.security.entities;

import java.time.Instant;

import az.codeworld.springboot.utilities.constants.emailstatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "EMAIL_OUTBOX",
    indexes = {
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_created_at", columnList = "created_at")
    }
)
public class EmailOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long outBoxId;

    @Column
    private String recipient;

    @Column
    private String subject;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String html;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private emailstatus status;

    @Column
    private int attempts;

    @Column
    private String lastError;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column
    private Instant sentAt;

    public static EmailOutbox pending(
        String to,
        String subject, 
        String html
    ) {
        EmailOutbox emailOutbox = new EmailOutbox();
        emailOutbox.setRecipient(to);
        emailOutbox.setSubject(subject);
        emailOutbox.setHtml(html);
        emailOutbox.setStatus(emailstatus.PENDING);
        emailOutbox.setAttempts(0);
        emailOutbox.setCreatedAt(Instant.now());

        return emailOutbox;
    }
}
