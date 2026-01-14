package az.codeworld.springboot.admin.mappers;

import java.time.LocalDate;
import java.util.Locale;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.dtos.UserDTO;
import az.codeworld.springboot.admin.dtos.create.UserCreateDTO;
import az.codeworld.springboot.admin.dtos.dashboard.UserDashboardDTO;
import az.codeworld.springboot.admin.dtos.transactions.UserTransactionDTO;
import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.security.dtos.LoginAuditDTO;
import az.codeworld.springboot.utilities.constants.dtotype;
import az.codeworld.springboot.utilities.constants.presence;

@Component
public class UserMapper {

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
                    .nextDate(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().plusMonths(1).getMonth(), user.getNextDate()).toString())
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
                    .build();
            case "DASHBOARD":
                return UserDashboardDTO
                    .builder()
                    .userName(user.getUserName())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .nextDate(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().plusMonths(1).getMonth(), user.getNextDate()).toString())
                    .payment(user.getPayment())
                    .isEmailAdded(user.hasEmail())
                    .isPhoneAdded(user.hasPhoneNumber())
                    .isBankAccountAdded(user.hasBankAccount())
                    .isCardAdded(user.hasCardNumber())
                    .completeness(user.getCompleteness())
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
