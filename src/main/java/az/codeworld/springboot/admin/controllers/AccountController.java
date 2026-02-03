package az.codeworld.springboot.admin.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import az.codeworld.springboot.admin.dtos.RequestDTO;
import az.codeworld.springboot.admin.dtos.UserDTO;
import az.codeworld.springboot.admin.dtos.auth.UserAuthDTO;
import az.codeworld.springboot.admin.dtos.dashboard.UserDashboardDTO;
import az.codeworld.springboot.admin.dtos.transactions.TransactionDTO;
import az.codeworld.springboot.admin.dtos.update.UserUpdateDTO;
import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.admin.records.TransactionLinkRecord;
import az.codeworld.springboot.admin.records.UserAuthRecord;
import az.codeworld.springboot.admin.services.LogoutService;
import az.codeworld.springboot.admin.services.RequestService;
import az.codeworld.springboot.admin.services.TransactionService;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.exceptions.InvalidRequestTokenException;
import az.codeworld.springboot.exceptions.UserNotFoundException;
import az.codeworld.springboot.security.auth.JpaUserDetails;
import az.codeworld.springboot.security.services.authservices.OtpService;
import az.codeworld.springboot.security.services.authservices.RegistrationService;
import az.codeworld.springboot.utilities.WriteLog;
import az.codeworld.springboot.utilities.constants.contenttypes;
import az.codeworld.springboot.utilities.constants.dtotype;
import az.codeworld.springboot.utilities.constants.mode;
import az.codeworld.springboot.utilities.constants.profileError;
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.web.dtos.ProfilePayloadDTO;
import az.codeworld.springboot.web.entities.ProfilePicture;
import az.codeworld.springboot.web.services.NotificationService;
import az.codeworld.springboot.web.services.ProfileService;
import az.codeworld.springboot.web.services.TimeZoneService;
import az.codeworld.springboot.web.services.serviceImpl.NotificationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.awt.image.BufferedImage;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final NotificationService notificationService;

    private final UserService userService;
    private final ProfileService profileService;

    private Logger log = LoggerFactory.getLogger(AccountController.class);

    private final RegistrationService registrationService;

    private final TransactionService transactionService;
    private final RequestService requestService;
    private final LogoutService logoutService;

    private final OtpService otpService;

    private final TimeZoneService timeZoneService;

    public AccountController(
            TransactionService transactionService,
            RequestService requestService,
            LogoutService logoutService,
            RegistrationService registrationService,
            UserService userService,
            ProfileService profileService,
            OtpService otpService,
            NotificationService notificationService,
            TimeZoneService timeZoneService
        ) {
        this.transactionService = transactionService;
        this.requestService = requestService;
        this.logoutService = logoutService;
        this.registrationService = registrationService;
        this.userService = userService;
        this.profileService = profileService;
        this.otpService = otpService;
        this.notificationService = notificationService;
        this.timeZoneService = timeZoneService;
    }

    @GetMapping({ "", "/" })
    public String account(
            @RequestParam(required = false, name = "personalDetails", defaultValue = "DISPLAY") String personalDetails,
            @RequestParam(required = false, name = "accountSettings", defaultValue = "DISPLAY") String accountSettings,
            @RequestParam(required = false, name = "emailAddresses", defaultValue = "DISPLAY") String emailAddresses,
            @RequestParam(required = false, name = "phone", defaultValue = "DISPLAY") String phone,
            @RequestParam(required = false, name = "section", defaultValue = "none") String section,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            Principal principal,
            Model model) {

        UserDTO userDTO = (UserDTO) userService.getUserByUserName(principal.getName(), dtotype.FULL);

        model.addAllAttributes(
                Map.of(
                        "userUpdateDTO", new UserUpdateDTO(),
                        "user", userDTO,
                        "personalDetails", personalDetails,
                        "accountSettings", accountSettings,
                        "emailAddresses", emailAddresses,
                        "phone", phone));

        if (fragment)
            switch (section) {
                case "personalDetails":
                    return "fragments/account/personal_details.html :: personal-details";
                case "accountSettings":
                    return "fragments/account/account_settings.html :: account-settings";
                case "emailAddresses":
                    return "fragments/account/email_addresses.html :: email-addresses";
                case "phone":
                    return "fragments/account/phone.html :: phone";
                default:
                    break;
            }

        return "user/account";
    }

    @GetMapping("/accountSecurity")
    public String accountSecurity(
            Principal principal,
            Model model) {
        // UserDashboardDTO userDashboardDTO = (UserDashboardDTO)
        // userService.getUserByUserName(principal.getName(), dtotype.DASHBOARD);

        // model.addAllAttributes(
        // Map.of(
        // "user", userDashboardDTO
        // )
        // );

        return "user/account_security";
    }

    @GetMapping("/notifications")
    public String notifications(
            Principal principal,
            Model model) {
        // UserDashboardDTO userDashboardDTO = (UserDashboardDTO)
        // userService.getUserByUserName(principal.getName(), dtotype.DASHBOARD);

        // model.addAllAttributes(
        // Map.of(
        // "user", userDashboardDTO
        // )
        // );

        return "user/notifications";
    }

    @PostMapping("/notifications/markRead/{id}")
    public String markRead(
            @PathVariable Long id,
            Principal principal,
            Model model) {

        UserDashboardDTO userDashboardDTO = (UserDashboardDTO) userService.getUserByUserName(principal.getName(),
                dtotype.DASHBOARD);

        notificationService.markRead(userDashboardDTO.getId(), id);

        // model.addAllAttributes(
        // Map.of(
        // "user", userDashboardDTO
        // )
        // );

        return "redirect:/account/notifications";
    }

    @PostMapping("/notifications/markAllRead")
    public String markAllRead(
            Principal principal,
            Model model) {

        UserDashboardDTO userDashboardDTO = (UserDashboardDTO) userService.getUserByUserName(principal.getName(),
                dtotype.DASHBOARD);

        notificationService.markAllRead(userDashboardDTO.getId());

        // model.addAllAttributes(
        // Map.of(
        // "user", userDashboardDTO
        // )
        // );

        return "redirect:/account/notifications";
    }

    // @GetMapping("/paymentMethods")
    // public String paymentMethods(
    // Principal principal,
    // Model model) {
    // // UserDTO userDTO = (UserDTO)
    // // userService.getUserByUserName(principal.getName(), dtotype.FULL);

    // // model.addAllAttributes(
    // // Map.of(
    // // "user", userDTO
    // // )
    // // );
    // return "user/payments";
    // }

    @GetMapping("/2fa")
    public String get2fa(
            Principal principal,
            Model model) {
        String userName = principal.getName();
        UserAuthRecord userAuthRecord = userService.getUserRecordByUserName(userName);
        String email = userAuthRecord.email();

        if (email != null) {
            otpService.createOtpCode(email);
            model.addAllAttributes(
                    Map.of(
                            "email", email,
                            "otpFormAction", "/account/2fa"));
            return "auth/otp-code/otp_code";
        } else {
            return "account-completeness/email";
        }
    }

    @PostMapping("/2fa")
    public String post2fa(
            @RequestParam("otpCode") String otpCode,
            @RequestParam("email") String email,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) {

        if (!otpService.isOtpValid(email, otpCode)) {
            model.addAllAttributes(
                    Map.of(
                            "email", email,
                            "otpFormAction", "/account/2fa"));
            return "redirect:/restricted/2fa?error=Invalid+ot+expired+code";
        }

        userService.enable2FA(email);

        return "redirect:/?success=2fa+successfully+activated!";
    }

    @PostMapping("/refreshOtpCode/{email}")
    public String refreshOtpCode(
            HttpServletRequest request,
            @PathVariable("email") String email,
            Model model) {
        otpService.createOtpCode(email);
        model.addAllAttributes(
                Map.of(
                        "email", email,
                        "otpFormAction", "/account/2fa"));

        return "redirect:/account/2fa";
    }

    @PostMapping("/deleteAccount")
    public String deleteAccount(
            Principal principal,
            HttpServletRequest request,
            HttpServletResponse response) {
   
        String username = principal.getName();

        Long userId = ((UserDashboardDTO) userService.getUserByUserName(username, dtotype.DASHBOARD))
                .getId();

        logoutService.exterminate(userId, request, response);

        userService.deleteUserById(userId);

        return "redirect:/restricted/?success=Account+Deleted";
    }

}
