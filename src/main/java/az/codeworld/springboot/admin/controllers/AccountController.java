package az.codeworld.springboot.admin.controllers;

import java.security.Principal;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import az.codeworld.springboot.admin.dtos.UserDTO;
import az.codeworld.springboot.admin.dtos.dashboard.UserDashboardDTO;
import az.codeworld.springboot.admin.dtos.update.UserUpdateDTO;
import az.codeworld.springboot.admin.records.UserAuthRecord;
import az.codeworld.springboot.admin.services.LogoutService;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.security.services.authservices.OtpService;
import az.codeworld.springboot.utilities.constants.dtotype;
import az.codeworld.springboot.web.services.NotificationService;
import az.codeworld.springboot.web.services.TimeZoneService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final NotificationService notificationService;

    private final UserService userService;
    private final LogoutService logoutService;

    private final OtpService otpService;

    private final TimeZoneService timeZoneService;

    public AccountController( 
            LogoutService logoutService,
            UserService userService,
            OtpService otpService,
            NotificationService notificationService,
            TimeZoneService timeZoneService
        ) {
        this.logoutService = logoutService;
        this.userService = userService;
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
        
        model.addAttribute("timeZones", timeZoneService.getTimeZones());

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
                            "otpFormAction", "/account/2fa",
                            "otpRefreshAction", "/restricted/refreshOtpCode"
                    ));
            return "auth/otp-code/otp_code";
        } else {
            return "account-completeness/email";
        }
    }

    @PostMapping("/2fa")
    public String post2fa(
        @RequestParam("otpCode") String otpCode,
        Principal principal,
        HttpServletRequest request,
        HttpServletResponse response,
        Model model
    ) {

        String email = userService.getUserRecordByUserName(principal.getName()).email();
        if (!otpService.isOtpValid(email, otpCode)) {
            model.addAllAttributes(Map.of(
                "email", email,
                "otpFormAction", "/account/2fa",
                "otpRefreshAction", "/account/refreshOtpCode",
                "error", "Invalid or expired code"
            ));
            return "auth/otp-code/otp_code";
        }

        userService.enable2FA(email);

        return "redirect:/account/accountSecurity?success=2fa+successfully+activated!";
    }

    @PostMapping("/2fa/unset")
    public String post2faUnset(
            Principal principal,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) {

        userService.disable2FA(principal.getName());

        return "redirect:/account/accountSecurity?success=2fa+successfully+disabled!";
    }

    @PostMapping("/refreshOtpCode")
    public String refreshOtpCode(
            @RequestParam(
                name = "email",
                required = true
            ) String email,
            Model model) {
        otpService.createOtpCode(email);
        model.addAllAttributes(
            Map.of(
                "email", email,
                "otpFormAction", "/account/2fa",
                "otpRefreshAction", "/account/refreshOtpCode"
            ));

        // return "redirect:/account/2fa";
        return "auth/otp-code/otp_code";
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
