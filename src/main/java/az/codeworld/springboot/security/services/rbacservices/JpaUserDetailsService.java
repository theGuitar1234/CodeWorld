package az.codeworld.springboot.security.services.rbacservices;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.admin.repositories.UserRepository;
import az.codeworld.springboot.security.entities.Authority;
import az.codeworld.springboot.security.entities.Role;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        User user = userOptional.orElseThrow(() -> new RuntimeException("User Not Found"));

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        Set<Role> roles = user.getRoles();
        
        for (Role role : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleNameString()));
            for (Authority authority : role.getAuthorities()) {
                grantedAuthorities.add(new SimpleGrantedAuthority(authority.getAuthorityNameString()));
            }
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                                                                                .withUsername(user.getUsername())
                                                                                .password(user.getPassword())
                                                                                .authorities(grantedAuthorities)
                                                                                .build();
        return userDetails;
    }
    
}
