package az.codeworld.springboot.security.auth.providers;

import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import az.codeworld.springboot.security.auth.JpaUserDetails;
import az.codeworld.springboot.security.services.JpaUserDetailsService;
import az.codeworld.springboot.utilities.constants.roles;

import org.springframework.security.core.GrantedAuthority;

@Component
public class DaoAuthenticationProvider implements AuthenticationProvider {

    private final JpaUserDetailsService jpaUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    public DaoAuthenticationProvider(JpaUserDetailsService jpaUserDetailsService, PasswordEncoder passwordEncoder) {
        this.jpaUserDetailsService = jpaUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        JpaUserDetails jpaUserDetails;
        
        try {
            jpaUserDetails = (JpaUserDetails) jpaUserDetailsService.loadUserByUsername(email);
        } catch (RuntimeException e) {
            throw new BadCredentialsException("UserName Not Found");
        }

        if (!jpaUserDetails.isEnabled())
            throw new DisabledException("User is banned");

        if (!jpaUserDetails.isAccountNonLocked())
            throw new LockedException("The Account is Locked");

        if (!passwordEncoder.matches(password, jpaUserDetails.getPassword()))
            throw new BadCredentialsException("Invalid Password");

        if (jpaUserDetails.isTwoFactorEnabled()) {
            List<GrantedAuthority> pre2faAuthority = List.of(
                new SimpleGrantedAuthority("ROLE_" + roles.PRE_2FA.getRoleNameString())
            );
            return new UsernamePasswordAuthenticationToken(jpaUserDetails, jpaUserDetails.getProtectedPassword(), pre2faAuthority);
        }

        return new UsernamePasswordAuthenticationToken(jpaUserDetails, jpaUserDetails.getProtectedPassword(), jpaUserDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
    
}
