package az.codeworld.springboot.security.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.security.entities.Authority;
import az.codeworld.springboot.security.entities.LoginAudit;
import az.codeworld.springboot.security.entities.Role;
import az.codeworld.springboot.utilities.configurations.ApplicationProperties;

public class JpaUserDetails implements UserDetails  {

    private final User user;
    private final ApplicationProperties applicationProperties;

    public JpaUserDetails(
        User user,
        ApplicationProperties applicationProperties
    ) {
        this.user = user;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public boolean isAccountNonLocked() {
        LoginAudit loginAudit = user.getLoginAudit();
        
        if (loginAudit == null) return true;

        return loginAudit.getBlockExpiry() <= System.currentTimeMillis();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        Set<Role> roles = user.getRoles();

        for (Role role : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleNameString()));
            for (Authority authority : role.getAuthorities()) {
                grantedAuthorities.add(new SimpleGrantedAuthority(authority.getAuthorityNameString()));
            }
        }
        return grantedAuthorities;
    }

    @Override
    public @Nullable String getPassword() {
        return applicationProperties.getLogin().getPassword().getPlaceholder();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }
    
}
