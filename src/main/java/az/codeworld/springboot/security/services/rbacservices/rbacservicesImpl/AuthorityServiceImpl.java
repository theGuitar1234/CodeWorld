package az.codeworld.springboot.security.services.rbacservices.rbacservicesImpl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import az.codeworld.springboot.security.entities.Authority;
import az.codeworld.springboot.security.repositories.AuthorityRepository;
import az.codeworld.springboot.security.services.rbacservices.AuthorityService;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;

    public AuthorityServiceImpl(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @Override
    public void saveAuthority(Authority authority) {
        authorityRepository.save(authority);
    }

    @Override
    public Authority getAuthorityById(Long authorityId) {
        Optional<Authority> authorityOptional = authorityRepository.findById(authorityId);
        Authority authority = authorityOptional.orElseThrow(() -> new RuntimeException("Authority Not Found"));
        return authority;
    }
    
}
