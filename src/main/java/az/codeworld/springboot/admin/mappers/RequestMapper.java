package az.codeworld.springboot.admin.mappers;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.dtos.RequestDTO;
import az.codeworld.springboot.utilities.constants.roles;

@Component
public class RequestMapper {
    public static RequestDTO toRequestDTO(
        Long requestId,
        String firstName,
        String lastName,
        String email,
        roles role,
        String requestToken
    ) {
        return RequestDTO
            .builder()
            .requestId(requestId)
            .firstName(firstName)
            .lastName(lastName)
            .email(email)
            .role(role)
            .requestToken(requestToken)
            .build();
    }
}
