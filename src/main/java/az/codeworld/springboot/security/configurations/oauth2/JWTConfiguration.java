package az.codeworld.springboot.security.configurations.oauth2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import az.codeworld.springboot.utilities.converters.KeycloakRealmRoleConverter;

@Configuration
public class JWTConfiguration {
    
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(
        KeycloakRealmRoleConverter keycloakRealmRoleConverter
    ) {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(keycloakRealmRoleConverter);
        return jwtAuthenticationConverter;
    }
}
