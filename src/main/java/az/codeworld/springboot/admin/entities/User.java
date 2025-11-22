package az.codeworld.springboot.admin.entities;

import java.time.LocalDateTime;
// import java.util.HashSet;

// import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
// import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
        @Index(name = "idx_first_last_name", columnList = "first_name, last_name")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uc_email", columnNames = { "email "}),
        @UniqueConstraint(name = "uc_password", columnNames = { "password" })
    }
)
@DiscriminatorColumn(name = "dtype")
@Inheritance(strategy = InheritanceType.JOINED)
abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName; 

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    @Pattern(regexp = "^[+994][[0-9]{3}]{2}[[0-9]{2}]{2}$")
    private String phoneNumber;

    @Column
    private LocalDateTime lastActiveAt;

    public void updateLastActiveAt() {
        this.lastActiveAt = LocalDateTime.now();
    }

    // @JsonIgnore
    // @ManyToMany(fetch = FetchType.EAGER)
    // @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    // private Set<Role> roles = new HashSet<>();

    // public void addRoleToUser(Role role) {
    //     this.roles.add(role);
    //     role.addUser(this);
    // }

    // public void removeRoleFromUser(Role role) {
    //     this.roles.remove(role);
    //     role.removeUser(this);
    // }

    // @JsonIgnore
    // @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    // private LoginAudit loginAudit;

    @Override
    public String toString() {
        return "User [userId=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", password="
                + "[PROTECTED]" + ", email=" + email + ", createdAt=" + createdAt + ", phoneNumber=" + phoneNumber + "]";
    }
}
