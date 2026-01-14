package az.codeworld.springboot.security.configurations.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.envers.repository.config.EnableEnversRepositories;

@Configuration
@EnableEnversRepositories(basePackages = "az.codeworld.springboot")
public class EnversConfiguration {
    
}
