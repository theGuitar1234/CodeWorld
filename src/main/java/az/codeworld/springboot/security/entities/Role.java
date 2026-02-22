package az.codeworld.springboot.security.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import az.codeworld.springboot.admin.entities.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(
    name = "ROLE",
    indexes = {
        @Index(name = "idx_role_name", columnList = "role_name")
    }
)
public class Role {
    @Id
    private Long roleId;

    @Column(name = "role_name", unique = true)
    private String roleNameString;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private List<User> users = new ArrayList<>();

    public void addUser(User user) {
       this.users.add(user);
    }
    public void removeUser(User user) {
        this.users.remove(user);
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "ROLE_AUTHORITIES",
        joinColumns = @JoinColumn(name = "roleId"),
        inverseJoinColumns = @JoinColumn(name = "authorityId")
    )
    private Set<Authority> authorities = new HashSet<>();
}
