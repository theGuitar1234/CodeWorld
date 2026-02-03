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
import az.codeworld.springboot.utilities.constants.authorities;
import az.codeworld.springboot.utilities.constants.roles;
import lombok.Builder;

@Builder
public class JpaUserDetails implements UserDetails  {

    private final String userName;
    private final String password;
    private final boolean isTwoFactorEnabled;
    private final Collection<? extends GrantedAuthority> baseAuthorities;
    private final LoginAudit loginAudit;
    private final ApplicationProperties applicationProperties = new ApplicationProperties();

    public JpaUserDetails(
        String userName,
        String password,
        boolean isTwoFactorEnabled,
        Collection<? extends GrantedAuthority> baseAuthorities,
        LoginAudit loginAudit
        //ApplicationProperties applicationProperties
    ) {
        this.userName = userName;
        this.password = password;
        this.isTwoFactorEnabled = isTwoFactorEnabled;
        this.baseAuthorities = baseAuthorities;
        this.loginAudit = loginAudit;
        //this.applicationProperties = applicationProperties;
    }

    // public JpaUserDetails(
    //     String userName,
    //     String password,
    //     boolean isTwoFactorEnabled,
    //     Collection<? extends GrantedAuthority> baseAuthorities,
    //     LoginAudit loginAudit
    // ) {
    //     this.userName = userName;
    //     this.password = password;
    //     this.isTwoFactorEnabled = isTwoFactorEnabled;
    //     this.baseAuthorities = baseAuthorities;
    //     this.loginAudit = loginAudit;
    //     //this.applicationProperties = new ApplicationProperties();
    // }

    public boolean isTwoFactorEnabled() {
        return isTwoFactorEnabled;
    }

    @Override
    public boolean isAccountNonLocked() {   
        if (loginAudit == null) return true;
        if (!loginAudit.isBlocked()) return true;

        Long expiry = loginAudit.getBlockExpiry();
        if (expiry == null) return false;

        return expiry <= System.currentTimeMillis();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return baseAuthorities;
    }

    public @Nullable String getProtectedPassword() {
        return applicationProperties.getLogin().getPassword().getPlaceholder();
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isEnabled() {
        return baseAuthorities
            .stream()
            .noneMatch(r -> ("ROLE_" + roles.BANNED.getRoleNameString()).equals(r.getAuthority()) || 
                authorities.NO_AUTHORITIES.getAuthorityString().equals(r.getAuthority())
        );
    }
    
}
