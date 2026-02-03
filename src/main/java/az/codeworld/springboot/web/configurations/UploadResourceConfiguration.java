package az.codeworld.springboot.web.configurations;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Serves files stored on disk under {user.dir}/${app.upload-root} at the URL path /uploads/**.
 *
 * This is required for the local profile picture feature to work.
 */
@Configuration
public class UploadResourceConfiguration implements WebMvcConfigurer {

    @Value("${app.upload-root:uploads}")
    private String uploadRoot;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path root = Paths.get(System.getProperty("user.dir"), uploadRoot).toAbsolutePath().normalize();
        String location = root.toUri().toString();
        if (!location.endsWith("/")) {
            location = location + "/";
        }

        registry
            .addResourceHandler("/uploads/**")
            .addResourceLocations(location)
            .setCachePeriod(3600);
    }
}
