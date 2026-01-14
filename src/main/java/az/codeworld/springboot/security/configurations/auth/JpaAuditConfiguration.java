package az.codeworld.springboot.security.configurations.auth;

import java.util.Optional;

// import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
// import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableJpaAuditing
public class JpaAuditConfiguration {
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.ofNullable(
            Optional
                .ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getName)
                .orElse("SYSTEM")
            );
    }
}
