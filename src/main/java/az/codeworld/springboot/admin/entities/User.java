package az.codeworld.springboot.admin.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import az.codeworld.springboot.security.entities.Role;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "USERS",
    indexes = {
        @Index(name = "idx_user_name", columnList = "user_name")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uc_email", columnNames = {"email"})
    }
)
// @DiscriminatorColumn(name = "dtype")
// @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    //@Pattern(regexp = "^[STA]-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]$")
    private String username;

    @NotBlank
    @Column(nullable = false)
    @Pattern(regexp = "^[A-Z]{1}[a-z]{1,14}$")
    private String firstName;

    @NotBlank
    @Column(nullable = false)
    @Pattern(regexp = "^[A-Z]{1}[a-z]{1,20}$")
    private String lastName; 

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    //@Pattern(regexp = "^+\\d{3} \\d{3} \\d{2} \\d{2}$")
    private String phoneNumber;

    @Past
    @Column
    private LocalDateTime lastActiveAt;

    public void updateLastActiveAt() {
        this.lastActiveAt = LocalDateTime.now();
    }

    @JsonIgnore
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

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
        transaction.setUser(this);
    }

    @Override
    public String toString() {
        return "User [userId=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", password="
                + "[PROTECTED]" + ", email=" + email + ", createdAt=" + createdAt + ", phoneNumber=" + phoneNumber + "]";
    }
}
