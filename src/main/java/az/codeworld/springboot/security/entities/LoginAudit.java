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

    @Column(length = 45)
    private String lastLoginIp;

    @Column(nullable = false)
    private int loginCount = 0;

    @Column(nullable = false)
    private int failedAttempts = 0;

    @Column(nullable = false)
    private boolean isBlocked = false;

    @Column
    private long blockExpiry = 0;

    @JsonIgnore
    @OneToOne
    @JoinColumn(
        name = "USER_EMAIL",
        referencedColumnName = "EMAIL",
        nullable = false,
        unique = true)
    private User user;
}
