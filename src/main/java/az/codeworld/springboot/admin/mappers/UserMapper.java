package az.codeworld.springboot.admin.mappers;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.h2.command.ddl.DeallocateProcedure;
import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.dtos.UserDTO;
import az.codeworld.springboot.admin.dtos.create.UserCreateDTO;
import az.codeworld.springboot.admin.dtos.dashboard.UserDashboardDTO;
import az.codeworld.springboot.admin.dtos.transactions.UserPayableDTO;
import az.codeworld.springboot.admin.dtos.transactions.UserTransactionDTO;
import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.security.dtos.LoginAuditDTO;
import az.codeworld.springboot.utilities.configurations.ApplicationProperties;
import az.codeworld.springboot.utilities.constants.dtotype;
import az.codeworld.springboot.utilities.constants.presence;
import az.codeworld.springboot.utilities.constants.roles;

@Component
public class UserMapper {

    private static final String DEFAULT_PROFILE_PHOTO = "/assets/sprites/profile-thumb.jpg";
    private static final String DATE_TIME_FORMAT = "dd-MM-yyyy";
    private static final String ZONE = "Asia/Baku";

    private static String resolveProfilePhoto(User user) {
        if (user == null) return DEFAULT_PROFILE_PHOTO;
        if (user.getProfilePicture() != null && user.getProfilePicture().getProfilePhoto() != null
                && !user.getProfilePicture().getProfilePhoto().isBlank()) {
            return user.getProfilePicture().getProfilePhoto();
        }
        return DEFAULT_PROFILE_PHOTO;
    }

    private static String resolvePasswordLastUpdatedAt(User user) {
        if (user == null) return "";
        if (user.getLoginAudit() != null && user.getLoginAudit().getPasswordLastUpdatedAt() != null) return user.getLoginAudit().getPasswordLastUpdatedAt().toString();
        return "";
    }

    public static Object toUserDTO(
        User user,
        String dtoTypeString
    ) {
        switch (dtoTypeString.toUpperCase(Locale.ROOT)) {
            case "FULL":
                return UserDTO
                    .builder()
                    .userName(user.getUserName())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .createdAt(user.getCreatedAt().toString())
                    .payment(user.getPayment())
                    .nextDate(LocalDate.ofInstant(user.getNextDate(), ZoneId.of(ZONE)).format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)).toString())
                    .birthDate(user.getBirthDate().toString())
                    .street(user.getStreet())
                    .city(user.getCity())
                    .region(user.getRegion())
                    .postalCode(user.getPostalCode())
                    .country(user.getCountry())
                    .language(user.getLanguage())
                    .timeZone(user.getTimeZone())
                    .phoneNumber(user.getPhoneNumber())
                    .presence(user.isOnline() ? presence.ONLINE.getPresenceString() : presence.OFFLINE.getPresenceString())
                    .profilePhoto(resolveProfilePhoto(user))
                    .isNearPayment(Instant.now().isAfter(user.getNextDate().minus(Duration.ofDays(2))))
                    .isPastPayment(Instant.now().isAfter(user.getNextDate()))
                    .build();
            case "DASHBOARD":
                return UserDashboardDTO
                    .builder()
                    .id(user.getId())
                    .userName(user.getUserName())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .nextDate(LocalDate.ofInstant(user.getNextDate(), ZoneId.of(ZONE)).format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)).toString())
                    .payment(user.getPayment())
                    .isEmailAdded(user.hasEmail())
                    .isPhoneAdded(user.hasPhoneNumber())
                    // .isBankAccountAdded(user.hasBankAccount())
                    // .isCardAdded(user.hasCardNumber())
                    .completeness(user.getCompleteness())
                    .profilePhoto(resolveProfilePhoto(user))
                    .passwordLastUpdatedAt(resolvePasswordLastUpdatedAt(user))
                    .isNearPayment(Instant.now().isAfter(user.getNextDate().minus(Duration.ofDays(2))))
                    .isPastPayment(Instant.now().isAfter(user.getNextDate()))
                    .build();
            case "TRANSACTION":
                return UserTransactionDTO
                    .builder()
                    .transactions(user.getTransactions())
                    .build();
            case "CREATE":
                return UserCreateDTO
                    .builder()
                    .username(user.getUserName())
                    .build();
            case "PAYABLE":
                return UserPayableDTO
                    .builder()
                    .id(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .payment(user.getPayment())
                    .nextDate(LocalDate.ofInstant(user.getNextDate(), ZoneId.of(ZONE)).format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)).toString())
                    .role(user.getRoles().stream().anyMatch(r -> roles.TEACHER.getRoleNameString().equals(r.getRoleNameString())) ? roles.TEACHER : roles.STUDENT)
                    .phoneNumber(user.getPhoneNumber())
                    .build();
            case "LOGIN_AUDIT":
                return LoginAuditDTO
                    .builder()
                    .isBlocked(user.getLoginAudit().isBlocked())
                    .blockExpiry(user.getLoginAudit().getBlockExpiry())
                    .build();
            default:
                throw new IllegalArgumentException("Unknown UserDTO type");
        }
    }
}
