package az.codeworld.springboot.admin.mappers;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.dtos.UserDTO;

@Component
public class UserMapper {
    public static UserDTO toUserDTO(
        String firstName,
        String lastName,
        String email,
        LocalDateTime createdAt
    ) {
        return UserDTO
            .builder()
            .firstName(firstName)
            .lastName(lastName)
            .email(email)
            .createdAt(email)
            .build();
    }
}
