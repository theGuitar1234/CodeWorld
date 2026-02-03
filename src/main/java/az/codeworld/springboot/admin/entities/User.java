package az.codeworld.springboot.admin.entities;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import az.codeworld.springboot.aop.validations.EmailValidation;
import az.codeworld.springboot.aop.validations.PasswordValidation;
import az.codeworld.springboot.aop.validations.UsernameValidation;
import az.codeworld.springboot.security.entities.AuditedEntity;
import az.codeworld.springboot.security.entities.LoginAudit;
import az.codeworld.springboot.security.entities.OtpCode;
import az.codeworld.springboot.security.entities.PasswordResetToken;
import az.codeworld.springboot.security.entities.Role;
import az.codeworld.springboot.utilities.constants.accountstatus;
import az.codeworld.springboot.web.entities.ProfilePicture;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
// import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Audited(withModifiedFlag = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USERS", indexes = {
        @Index(name = "idx_user_name", columnList = "user_name")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uc_email", columnNames = { "email" })
})
// @DiscriminatorColumn(name = "dtype")
// @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends AuditedEntity {

    private final String ZONE = "Asia/Baku";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_generator")
    @SequenceGenerator(name = "user_id_generator", sequenceName = "user_id_sequence", allocationSize = 50, initialValue = 1000)
    private Long id;

    @NotNull
    @NotBlank
    // @Length(min = 13, max = 13)
    @Column(nullable = false, unique = true)
    //@UsernameValidation
    private String userName;

    @NotBlank
    @Column(nullable = false)
    @Pattern(regexp = "^[A-Z]{1}[a-z]{1,14}$")
    private String firstName;

    @NotBlank
    @Column(nullable = false)
    @Pattern(regexp = "^[A-Z]{1}[a-z]{1,20}$")
    private String lastName;

    @Column(unique = true)
    @EmailValidation
    private String email;

    @Column
    private boolean isTwoFactorEnabled = false;

    @Column(nullable = false)
    @PasswordValidation
    private String password;

    @CreationTimestamp
    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = true)
    @Pattern(regexp = "^\\+\\d{3}\\d{2}\\d{3}\\d{2}\\d{2}$")
    private String phoneNumber;

    @Column(nullable = true)
    @Min(0)
    @Max(150)
    private Integer age;

    @Past
    @Column
    private LocalDate birthDate;

    @Column
    private String street;

    @Column
    private String city;

    @Column
    private String region;

    @Column
    private int postalCode;

    @Column
    private String country;

    @Column
    @Enumerated(EnumType.STRING)
    private accountstatus accountStatus;

    @Column
    private boolean isBanned = false;

    @Column
    private String language;

    @Column
    private String zoneId;

    // @Column
    // private String countryId;

    // @Column(nullable = true)
    // @Pattern(regexp = "^\\d{4}\\d{4}\\d{4}\\d{4}")
    // private String cardNumber;

    // @Column(nullable = true)
    // private String bankAccount;

    @NotNull
    @Column
    private Instant nextDate;

    @Column
    private boolean billingEnabled;

    public void updateNextPaymentDate() {
        this.nextDate = LocalDate
            .ofInstant(nextDate == null ? Instant.now() : nextDate, ZoneId.of(ZONE))
            .plusMonths(1)
            .atStartOfDay(ZoneId.of(ZONE)).toInstant();
    }

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "salary_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "salary_currency"))
    })
    private Money payment;

    @Past
    @Column
    private Instant lastActiveAt;

    //https://imagedelivery.net/<ACCOUNT_HASH>/<IMAGE_ID>/<VARIANT>
    @Column
    private String profileImageId;
    
    @Column
    private Instant profileImageUpdatedAt;

    @Column
    private LocalDate affiliationDate;

    @NotAudited
    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private ProfilePicture profilePicture;

    public void updateLastActiveAt() {
        this.lastActiveAt = Instant.now();
    }

    public boolean isOnline() {
        if (this.lastActiveAt == null)
            return false;
        return this.lastActiveAt.isAfter(Instant.now().minus(Duration.ofMinutes(5)));
    }

    public String getTimeZone() {
        String zone = this.zoneId.substring(this.zoneId.lastIndexOf(' ') + 1);
        ZoneId zoneId = ZoneId.of(zone == null ? ZONE : zone);
        ZoneOffset zoneOffset = zoneId.getRules().getOffset(Instant.now());

        // String city = zoneId.getId().contains("/")
        //         ? zoneId.getId().substring(zoneId.getId().lastIndexOf('/') + 1).replace('_', ' ')
        //         : zoneId.getId();

        boolean isUtc = zoneOffset.equals(ZoneOffset.UTC);

        String prefix = isUtc ? "UTC" : "GMT";
        String offText = isUtc ? "+00:00" : zoneOffset.getId();

        return "(" + prefix + offText + ") " + zoneId.getId();
    }

    public boolean hasPhoneNumber() {
        return this.phoneNumber != null && !this.phoneNumber.isBlank();
    }

    public boolean hasEmail() {
        return this.email != null && !this.email.isBlank();
    }

    // public boolean hasCardNumber() {
    //     return this.cardNumber != null && !this.cardNumber.isBlank();
    // }

    // public boolean hasBankAccount() {
    //     return this.bankAccount != null && !this.bankAccount.isBlank();
    // }

    @PrePersist
    @PreUpdate
    private void normalize() {
        this.phoneNumber = normalizeToNull(phoneNumber);
        this.email = normalizeToNull(email);
        // this.cardNumber = normalizeToNull(cardNumber);
        // this.bankAccount = normalizeToNull(bankAccount);
    }

    private String normalizeToNull(String s) {
        if (s == null)
            return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    public byte getCompleteness() {
        return (byte) (((
                (this.hasEmail() ? 1 : 0) + 
                (this.hasPhoneNumber() ? 1 : 0) 
                // + (this.hasCardNumber() ? 1 : 0) + 
                // (this.hasBankAccount() ? 1 : 0)
            ) / 2.0) * 100.0);
    }

    // @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @NotAudited
    @JsonIgnore
    // @NotEmpty
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "USERS_ROLES", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        this.roles.add(role);
        role.addUser(this);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
        role.removeUser(this);
    }

    @NotAudited
    @JsonIgnore
    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.MERGE)
    private List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
        transaction.setUser(this);
    }

    @NotAudited
    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private PasswordResetToken passwordResetToken;

    @NotAudited
    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private OtpCode otpCode;

    @NotAudited
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private LoginAudit loginAudit;

    @Override
    public String toString() {
        return "User [userId=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", userName=" + userName
                + ", password="
                + "[PROTECTED]" + ", email=" + email + ", createdAt=" + createdAt + ", phoneNumber=" + phoneNumber
                + "]";
    }
}
