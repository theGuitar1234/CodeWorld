package az.codeworld.springboot.security.services;

import org.springframework.stereotype.Service;

import az.codeworld.springboot.security.entities.Authority;

@Service
public interface AuthorityService {
    void saveAuthority(Authority authority);
    Authority getAuthorityById(Long authorityId);
}
