package az.codeworld.springboot.security.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.security.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @EntityGraph(attributePaths = "authorities")
    Optional<Role> findByRoleNameString(String roleNameString);
}