package az.codeworld.springboot.admin.mappers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.dtos.RequestDTO;
import az.codeworld.springboot.utilities.configurations.ApplicationProperties;
import az.codeworld.springboot.utilities.constants.roles;

@Component
public class RequestMapper {

    private final ApplicationProperties applicationProperties;

    RequestMapper(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    //private static final String ZONE = "Asia/Baku";

    public static RequestDTO toRequestDTO(
        Long requestId,
        String firstName,
        String lastName,
        String email,
        roles role,
        String requestToken,
        long expiresAt
    ) {
        ApplicationProperties applicationProperties = new ApplicationProperties();
        return RequestDTO
            .builder()
            .requestId(requestId)
            .firstName(firstName)
            .lastName(lastName)
            .email(email)
            .role(role)
            .requestToken(requestToken)
            .requestId(requestId)
            .expiresAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(expiresAt), ZoneId.of(applicationProperties.getTime().getZone())).format(DateTimeFormatter.ofPattern(applicationProperties.getTime().getDateTimeFormat())))
            .build();
    }
}
