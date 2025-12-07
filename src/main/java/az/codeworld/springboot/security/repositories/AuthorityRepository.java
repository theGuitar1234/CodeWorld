package az.codeworld.springboot.security.repositories;

import org.springframework.stereotype.Repository;

import az.codeworld.springboot.security.entities.Authority;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long>{

}
