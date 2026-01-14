package az.codeworld.springboot.security.configurations.oauth2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import az.codeworld.springboot.utilities.converters.KeycloakRealmRoleConverter;

@Configuration
public class KeyCloakConfiguraion {
    
    @Bean
    KeycloakRealmRoleConverter keycloakRealmRoleConverter(
        @Value("${KEYCLOAK_CLIENT_ID:") String clientId
    ) {
        return new KeycloakRealmRoleConverter(clientId);
    }
}
