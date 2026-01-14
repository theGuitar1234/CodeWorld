package az.codeworld.springboot.security.services.rbacservices;

import org.springframework.stereotype.Service;

import az.codeworld.springboot.security.entities.Authority;

public interface AuthorityService {
    void saveAuthority(Authority authority);
    Authority getAuthorityById(Long authorityId);
}
