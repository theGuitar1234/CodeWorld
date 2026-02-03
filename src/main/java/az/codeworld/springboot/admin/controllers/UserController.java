package az.codeworld.springboot.admin.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import az.codeworld.springboot.admin.dtos.RequestDTO;
import az.codeworld.springboot.admin.dtos.UserDTO;
import az.codeworld.springboot.admin.dtos.auth.UserAuthDTO;
import az.codeworld.springboot.admin.dtos.dashboard.UserDashboardDTO;
import az.codeworld.springboot.admin.dtos.transactions.TransactionDTO;
import az.codeworld.springboot.admin.dtos.update.UserUpdateDTO;
import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.admin.projections.UserAdminProjection;
import az.codeworld.springboot.admin.projections.UserLogoutProjection;
import az.codeworld.springboot.admin.records.TransactionLinkRecord;
import az.codeworld.springboot.admin.services.LogoutService;
import az.codeworld.springboot.admin.services.RequestService;
import az.codeworld.springboot.admin.services.TransactionService;
import az.codeworld.springboot.admin.services.UserService;

import az.codeworld.springboot.exceptions.InvalidRequestTokenException;
import az.codeworld.springboot.exceptions.UserNotFoundException;

import az.codeworld.springboot.security.services.authservices.RegistrationService;
import az.codeworld.springboot.utilities.WriteLog;
import az.codeworld.springboot.utilities.configurations.ApplicationProperties;
import az.codeworld.springboot.utilities.constants.contenttypes;
import az.codeworld.springboot.utilities.constants.dtotype;
import az.codeworld.springboot.utilities.constants.exceptionmessages;
import az.codeworld.springboot.utilities.constants.mode;
import az.codeworld.springboot.utilities.constants.profileError;
import az.codeworld.springboot.utilities.constants.roles;

import az.codeworld.springboot.web.dtos.ProfilePayloadDTO;
import az.codeworld.springboot.web.entities.ProfilePicture;
import az.codeworld.springboot.web.services.ProfileService;
import az.codeworld.springboot.web.services.TimeZoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.awt.image.BufferedImage;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final ProfileService profileService;
    private final TimeZoneService timeZoneService;

    private Logger log = LoggerFactory.getLogger(UserController.class);

    private final RegistrationService registrationService;

    private final TransactionService transactionService;
    private final RequestService requestService;
    private final LogoutService logoutService;

    private final ApplicationProperties applicationProperties;

    public UserController(
            TransactionService transactionService,
            RequestService requestService,
            LogoutService logoutService,
            RegistrationService registrationService,
            UserService userService,
            ProfileService profileService,
            ApplicationProperties applicationProperties,
            TimeZoneService timeZoneService) {
        this.transactionService = transactionService;
        this.requestService = requestService;
        this.logoutService = logoutService;
        this.registrationService = registrationService;
        this.userService = userService;
        this.profileService = profileService;
        this.applicationProperties = applicationProperties;
        this.timeZoneService = timeZoneService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/restricted";
    }

    @GetMapping("/logout")
    public String logout(Principal principal, HttpServletRequest request, HttpServletResponse response) {
        logoutService.exterminate(
                (userService.getUserProjectionByUserName(principal.getName(), UserLogoutProjection.class)).getId(),
                request,
                response);
        return "redirect:/restricted/";
    }

    @GetMapping("/register")
    public String register(
            @RequestParam(required = true, name = "token") String token,
            HttpServletResponse response,
            Model model) throws IOException {
        try {
            RequestDTO requestDTO = requestService.validateRequest(token);
            model.addAttribute("request", requestDTO);
            return "auth/registration/register";
        } catch (InvalidRequestTokenException e) {
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
            return null;
        }
    }

    @PostMapping("/register")
    public String register(
            @RequestParam(required = true, name = "token") String token,
            @RequestParam(required = true, name = "password") String password,
            HttpServletResponse response) throws IOException {
        try {
            RequestDTO requestDTO = requestService.validateRequest(token);
            registrationService.registerUser(UserAuthDTO
                    .builder()
                    .firstName(requestDTO.getFirstName())
                    .lastName(requestDTO.getLastName())
                    .email(requestDTO.getEmail())
                    .role(requestDTO.getRole())
                    .password(password)
                    .build());
            requestService.deleteRequestByRequestId(requestDTO.getRequestId());
        } catch (InvalidRequestTokenException e) {
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
            return null;
        }
        return "redirect:/restricted/";
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {

        UserDashboardDTO userDashboardDTO = (UserDashboardDTO) userService.getUserByUserName(principal.getName(),
                dtotype.DASHBOARD);

        model.addAllAttributes(
                Map.of(
                        // "user", userDashboardDTO,
                        "transactions", transactionService.getRecentTransactions(userDashboardDTO.getId()),
                        "transactionsOnPage", Page.empty(),
                        "pages", Page.empty(),
                        "mode", "PREVIEW"));
        return "dashboard/dashboard.html";
    }

    @GetMapping("/transactions")
    public String transactions(
            @RequestParam(required = false, name = "sortBy", defaultValue = "transactionAmount") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(required = false, name = "role", defaultValue = "TEACHER") roles role,
            @RequestParam(required = false, name = "mode", defaultValue = "PREVIEW") String mode,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            @RequestParam(required = false, name = "report", defaultValue = "false") boolean report,
            @RequestParam(required = false, name = "startDate") LocalDate startDate,
            @RequestParam(required = false, name = "endDate") LocalDate endDate,
            Model model,
            Principal principal) {

        UserDashboardDTO userDashboardDTO = (UserDashboardDTO) userService.getUserByUserName(principal.getName(),
                dtotype.DASHBOARD);

        Page<TransactionDTO> transactionsOnPage = transactionService
                .getPaginatedTransactions(
                        Optional.ofNullable(startDate).orElseGet(() -> LocalDate.ofEpochDay(0))
                                .atStartOfDay(ZoneId.of(applicationProperties.getTime().getZone())).toInstant(),
                        Optional.ofNullable(endDate).orElseGet(() -> LocalDate.now())
                                .plusDays(1)
                                .atStartOfDay(ZoneId.of(applicationProperties.getTime().getZone())).toInstant(),
                        userDashboardDTO.getId(),
                        pageIndex - 1,
                        perPage,
                        sortBy,
                        direction);

        TransactionLinkRecord linkRecord;
        List<TransactionLinkRecord> pages = new ArrayList<>();
        for (int i = 0; i < transactionsOnPage.getTotalPages(); i++) {
            String isActive = "";
            if (i == transactionsOnPage.getNumber()) {
                isActive = "current";
            }
            linkRecord = new TransactionLinkRecord(isActive, perPage, i + 1, direction, role);
            pages.add(linkRecord);
        }

        model.addAttribute("transactions", transactionsOnPage.getContent());
        model.addAttribute("transactionsOnPage", transactionsOnPage);
        model.addAttribute("pages", pages);

        model.addAttribute("perPage", perPage);
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("role", role);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction.name());
        model.addAttribute("startDate", startDate); 
        model.addAttribute("endDate", endDate);

        model.addAttribute("mode", "VIEW_ALL");

        WriteLog.main("pageIndex=" + pageIndex
            + " perPage=" + perPage
            + " returned=" + transactionsOnPage.getNumberOfElements()
            + " totalElements=" + transactionsOnPage.getTotalElements()
            + " totalPages=" + transactionsOnPage.getTotalPages()
            + " userId=" + userDashboardDTO.getId()
            + " start=" + startDate
            + " endExclusive=" + endDate, UserController.class);

        if (report)
            return transactionsOnPage.get().toString();
        if (fragment)
            return "fragments/transaction-pagination.html :: transaction-pagination";

        return "dashboard/dashboard.html";
    }

    @ResponseBody
    @GetMapping("/transactions/getReport")
    public ResponseEntity<?> getReport(
            @RequestParam(required = false, name = "sortBy", defaultValue = "transactionAmount") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(required = false, name = "role", defaultValue = "TEACHER") roles role,
            @RequestParam(required = false, name = "mode", defaultValue = "PREVIEW") String mode,
            @RequestParam(required = false, name = "startDate") LocalDate startDate,
            @RequestParam(required = false, name = "endDate") LocalDate endDate,
            Principal principal) {

        UserDashboardDTO userDashboardDTO = (UserDashboardDTO) userService.getUserByUserName(principal.getName(),
                dtotype.DASHBOARD);

        Page<TransactionDTO> transactionsOnPage = transactionService
                .getPaginatedTransactions(
                        Optional.ofNullable(startDate).orElseGet(() -> LocalDate.ofEpochDay(0))
                                .atStartOfDay(ZoneId.of(applicationProperties.getTime().getZone())).toInstant(),
                        Optional.ofNullable(endDate).orElseGet(() -> LocalDate.now().plusDays(1))
                                .atStartOfDay(ZoneId.of(applicationProperties.getTime().getZone())).toInstant(),
                        userDashboardDTO.getId(),
                        pageIndex - 1,
                        perPage,
                        sortBy,
                        direction);

        return ResponseEntity.ok(transactionsOnPage.get().toList());
    }

    @GetMapping("/account")
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

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setFirstName(userDTO.getFirstName());
        userUpdateDTO.setLastName(userDTO.getLastName());
        userUpdateDTO.setEmail(userDTO.getEmail());
        userUpdateDTO.setBirthDate(userDTO.getBirthDate());
        userUpdateDTO.setStreet(userDTO.getStreet());
        userUpdateDTO.setCity(userDTO.getCity());
        userUpdateDTO.setRegion(userDTO.getRegion());
        userUpdateDTO.setPostalCode(userDTO.getPostalCode());
        userUpdateDTO.setCountry(userDTO.getCountry());
        userUpdateDTO.setLanguage(userDTO.getLanguage());
        userUpdateDTO.setTimeZone(userDTO.getTimeZone());
        userUpdateDTO.setPhoneNumber(userDTO.getPhoneNumber());
        userUpdateDTO.setAge(userDTO.getAge());

        model.addAllAttributes(
                Map.of(
                        "userUpdateDTO", userUpdateDTO,
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

    @PostMapping("/updateUser")
    public String updateUser(
            @ModelAttribute("userUpdateDTO") @Valid UserUpdateDTO userUpdateDTO,
            Principal principal,
            Model model,
            @RequestParam(required = false, name = "personalDetails", defaultValue = "DISPLAY") String personalDetails,
            @RequestParam(required = false, name = "accountSettings", defaultValue = "DISPLAY") String accountSettings,
            @RequestParam(required = false, name = "emailAddresses", defaultValue = "DISPLAY") String emailAddresses,
            @RequestParam(required = false, name = "phone", defaultValue = "DISPLAY") String phone,
            @RequestParam(required = false, name = "section", defaultValue = "none") String section)
            throws UserNotFoundException {

        UserDTO userDTO = userService.updateUser(userUpdateDTO, principal.getName());

        model.addAllAttributes(
                Map.of(
                        "user", userDTO,
                        "personalDetails", personalDetails,
                        "accountSettings", accountSettings,
                        "emailAddresses", emailAddresses,
                        "phone", phone));

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

    @PostMapping("/updatePassword")
    public String updatePassword(
            Principal principal,
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam String password,
            @RequestParam String password2) {
        String userName = principal.getName();
        try {
            Long userId = null;
            if (password.equals(password2)) {
                userId = userService.updatePassword(userName, password);
            }
            logoutService.exterminate(userId, request, response);
            return "redirect:/restricted/?success=User%20Logged%20Out";
        } catch (RuntimeException e) {
            e.printStackTrace();
            return "user/account_security?error=" + exceptionmessages.getDefault();
        }
    }

    @PostMapping(value = "/addProfilePicture", consumes = "Application/JSON")
    public ResponseEntity<?> addProfilePicture(
            @Valid @RequestBody ProfilePayloadDTO profilePayloadDTO,
            @RequestParam Long profileId,
            @RequestPart(required = true) MultipartFile multipartFile) {
        try {

            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            UserDTO userDTO = (UserDTO) userService.getUserByUserName(username, dtotype.FULL);

            ProfilePicture profilePicture = new ProfilePicture();
            profilePicture.setProfileTitle(profilePayloadDTO.getProfileTitle());
            profilePicture.setDescription(profilePayloadDTO.getDescription());

            profileService.addProfilePictureToUser(profilePicture);

            if (multipartFile == null)
                return ResponseEntity.badRequest().body("No File selected");

            Path uploadDirectory = Paths.get(System.getProperty("user.dir"), "photos");

            if (!Files.exists(uploadDirectory))
                Files.createDirectory(uploadDirectory);

            ProfilePicture profilePicture2 = profileService.getProfileByProfileId(profileId);

            Path saveFile;

            switch (multipartFile.getContentType()) {
                case "image/jpeg":
                    saveFile = uploadDirectory
                            .resolve(UUID.randomUUID().toString() + contenttypes.JPEG.getContentTypeString());
                    break;
                case "image/png":
                    saveFile = uploadDirectory
                            .resolve(UUID.randomUUID().toString() + contenttypes.PNG.getContentTypeString());
                default:
                    return ResponseEntity.badRequest().body("Unsupported File Format");
            }

            BufferedImage img = ImageIO.read(saveFile.toFile());
            BufferedImage thumbImg = Scalr.resize(img, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, 300,
                    Scalr.OP_ANTIALIAS);

            Path thumbnailsDirectory = Paths.get(System.getProperty("user.dir"), "uploads/thumbnails");

            if (!Files.exists(thumbnailsDirectory))
                Files.createDirectory(thumbnailsDirectory);

            String filename = UUID.randomUUID().toString() + contenttypes.PNG.getContentTypeString();

            Path thumbnail = thumbnailsDirectory.resolve(filename);
            ImageIO.write(thumbImg, multipartFile.getContentType().split("/")[1], thumbnail.toFile());

            profilePicture2.setThumbnail(thumbnail.toString());

            profileService.saveProfilePicture(profilePicture2);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(profilePayloadDTO);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(profileError.PROFILE_ERROR.getProfileErrorString() + ' ' + e);
        }
    }

    @PostMapping(value = "/updateProfilePicture/{userName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String updateProfilePicture(
            @PathVariable("userName") String userName,
            @RequestParam("profilePicture") MultipartFile file,
            Principal principal) throws IOException {

        if (principal == null || principal.getName() == null || !principal.getName().equals(userName)) {
            return "redirect:/user/dashboard?avatar=forbidden";
        }

        if (file == null || file.isEmpty()) {
            return "redirect:/user/dashboard?avatar=empty";
        }

        String contentType = file.getContentType();
        String ext;
        String format;

        if ("image/jpeg".equalsIgnoreCase(contentType) || "image/jpg".equalsIgnoreCase(contentType)) {
            ext = contenttypes.JPEG.getContentTypeString();
            format = "jpg";
        } else if ("image/png".equalsIgnoreCase(contentType)) {
            ext = contenttypes.PNG.getContentTypeString();
            format = "png";
        } else {
            return "redirect:/user/dashboard?avatar=unsupported";
        }

        ProfilePicture profile = profileService.getOrCreateForUser(userName);

        Path root = Paths.get(System.getProperty("user.dir"), "uploads");
        Path photosDir = root.resolve("profile");
        Path thumbsDir = root.resolve(Paths.get("profile", "thumbs"));
        Files.createDirectories(photosDir);
        Files.createDirectories(thumbsDir);

        deleteLocalDefault(profile.getProfilePhoto(), root);
        deleteLocalDefault(profile.getThumbnail(), root);

        String fileName = UUID.randomUUID().toString() + ext;
        Path photoPath = photosDir.resolve(fileName).normalize();

        Files.copy(file.getInputStream(), photoPath, StandardCopyOption.REPLACE_EXISTING);

        BufferedImage img = ImageIO.read(photoPath.toFile());
        if (img != null) {
            BufferedImage thumbImg = Scalr.resize(
                    img,
                    Scalr.Method.QUALITY,
                    Scalr.Mode.AUTOMATIC,
                    300,
                    Scalr.OP_ANTIALIAS);

            String thumbName = UUID.randomUUID().toString() + contenttypes.PNG.getContentTypeString();
            Path thumbPath = thumbsDir.resolve(thumbName).normalize();
            ImageIO.write(thumbImg, "png", thumbPath.toFile());

            profile.setThumbnail("/uploads/profile/thumbs/" + thumbName);
        } else {
            profile.setThumbnail(profile.getThumbnail());
        }

        profile.setProfilePhoto("/uploads/profile/" + fileName);
        profileService.saveProfilePicture(profile);

        return "redirect:/user/dashboard?avatar=updated";
    }

    private void deleteLocalDefault(String urlPath, Path uploadsRoot) {
        if (urlPath == null || urlPath.isBlank())
            return;
        if (!urlPath.startsWith("/uploads/"))
            return;
        try {
            Path p = uploadsRoot.resolve(urlPath.substring(1).replaceFirst("^uploads/", "")).normalize();

            if (!p.toAbsolutePath().startsWith(uploadsRoot.toAbsolutePath()))
                return;
            Files.deleteIfExists(p);
        } catch (Exception e) {
        }
    }

}
