package az.codeworld.springboot.security.entities;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

import az.codeworld.springboot.admin.entities.User;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "LOGIN_AUDIT")
public class LoginAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    @Column
    private Instant lastLoginAt;

    @Column
    private Instant passwordLastUpdatedAt;

    public void updatePasswordLastUpdatedAt() {
        this.passwordLastUpdatedAt = Instant.now();
    }

    @Column(length = 45)
    private String lastLoginIp;

    @Column(nullable = false)
    private int loginCount = 0;

    @Column(nullable = false)
    private int failedAttempts = 0;

    @Column(nullable = false)
    private boolean isBlocked = false;

    @Column
    private Long blockExpiry;

    @JsonIgnore
    @OneToOne
    @JoinColumn(
        name = "USER_ID",
        referencedColumnName = "id",
        nullable = false,
        unique = true)
    private User user;
}
