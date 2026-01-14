package az.codeworld.springboot.security.entities;

import az.codeworld.springboot.admin.entities.User;

// import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "OTP_CODES")
public class OtpCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long otpId;  

    @Column(name = "OTP_CODE", nullable = false, length = 6)
    private String otpCode;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "EXPIRATION_DATE", nullable = false)
    private long expiresAtEpochMillis;

    @OneToOne
    @JoinColumn(name = "USER_ID", unique = true, nullable = false)
    private User user;
}
