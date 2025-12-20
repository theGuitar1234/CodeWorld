package az.codeworld.springboot.web.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnviromentConfiguration {
    
    @Value("${server.port:8080}")
    private String port;

    @Bean
    public String getPort() {
        return port;
    }
}
