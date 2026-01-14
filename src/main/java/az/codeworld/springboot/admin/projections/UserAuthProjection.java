package az.codeworld.springboot.admin.projections;

import org.springframework.stereotype.Component;

@Component
public interface UserAuthProjection {
    String getEmail();
    String getPassword();
}
