package az.codeworld.springboot.admin.configurations;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Index API",
        version = "1.0",
        contact = @Contact(
            name = "The Guitar", 
            email = "turanlizad776@gmail.com", 
            url = "https://sore-loser.com"
        ),
        license = @License(
            name = "Apache 2.0", 
            url = "http://www.apache.org/licenses/LICENSE-2.0"
        ),
        termsOfService = "https://sore-loser.com",
        description = "Lame Spring Boot Application by the most sore loser ever"
    )
)
public class OpenAPIConfiguration {
    
}
