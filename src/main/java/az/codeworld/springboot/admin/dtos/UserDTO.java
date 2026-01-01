package az.codeworld.springboot.admin.dtos;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String createdAt;

    public UserDTO(
        String username,
        String firstname,
        String lastName,
        String email,
        LocalDateTime createdAt
    ) {
        this.username = username;
        this.createdAt = createdAt.toString();
        this.email = email;
        this.firstName = firstname;
        this.lastName = lastName;
    }
}
