package az.codeworld.springboot.admin.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import az.codeworld.springboot.admin.dtos.RequestDTO;
import az.codeworld.springboot.admin.dtos.UserDTO;
import az.codeworld.springboot.admin.dtos.dashboard.UserDashboardDTO;
import az.codeworld.springboot.admin.dtos.transactions.TransactionDTO;
import az.codeworld.springboot.admin.dtos.update.UserUpdateDTO;
import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.admin.records.LinkRecord;
import az.codeworld.springboot.admin.services.LogoutService;
import az.codeworld.springboot.admin.services.RequestService;
import az.codeworld.springboot.admin.services.TransactionService;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.exceptions.InvalidRequestTokenException;
import az.codeworld.springboot.exceptions.UserNotFoundException;
import az.codeworld.springboot.security.dtos.UserAuthDTO;
import az.codeworld.springboot.security.services.authservices.RegistrationService;
import az.codeworld.springboot.utilities.constants.contenttypes;
import az.codeworld.springboot.utilities.constants.dtotype;
import az.codeworld.springboot.utilities.constants.mode;
import az.codeworld.springboot.utilities.constants.profileError;
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.web.dtos.ProfilePayloadDTO;
import az.codeworld.springboot.web.entities.ProfilePicture;
import az.codeworld.springboot.web.services.ProfileService;
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

    private Logger log = LoggerFactory.getLogger(UserController.class);

    private final RegistrationService registrationService;

    private final TransactionService transactionService;
    private final RequestService requestService;
    private final LogoutService logoutService;

    public UserController(
        TransactionService transactionService,
        RequestService requestService,
        LogoutService logoutService,
        RegistrationService registrationService,
        UserService userService,
        ProfileService profileService
    ) {
        this.transactionService = transactionService;
        this.requestService = requestService;
        this.logoutService = logoutService;
        this.registrationService = registrationService;
        this.userService = userService;
        this.profileService = profileService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/restricted";
    }

    @GetMapping("/logout")
    public String logout(Principal principal, HttpServletRequest request, HttpServletResponse response) {
        logoutService.exterminate(principal.getName(), request, response);
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

        System.out.println("\n\n\n\n\n\n\n\n" + userDashboardDTO.getCompleteness() + "\n\n\n\n\n\n\n\n");

        model.addAllAttributes(
                Map.of(
                        "user", userDashboardDTO,
                        "transactions", transactionService.getRecentTransactions(roles.TEACHER),
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
            Model model,
            Principal principal) {

        UserDashboardDTO userDashboardDTO = (UserDashboardDTO) userService.getUserByUserName(principal.getName(),
                dtotype.DASHBOARD);

        Page<TransactionDTO> transactionsOnPage = transactionService
                .getPaginatedTransactions(
                        role,
                        pageIndex - 1,
                        perPage,
                        sortBy,
                        direction);

        LinkRecord linkRecord;
        List<LinkRecord> pages = new ArrayList<>();
        for (int i = 0; i < transactionsOnPage.getTotalPages(); i++) {
            String isActive = "";
            if (i == transactionsOnPage.getNumber()) {
                isActive = "current";
            }
            linkRecord = new LinkRecord(isActive, perPage, i + 1, direction, role);
            pages.add(linkRecord);
        }

        model.addAllAttributes(
                Map.of(
                        "user", userDashboardDTO,
                        "transactions", transactionsOnPage.getContent(),
                        "transactionsOnPage", transactionsOnPage,
                        "mode", "VIEW_ALL",
                        "pages", pages));

        if (fragment)
            return "fragments/transaction-pagination.html :: transaction-pagination";

        return "dashboard/dashboard.html";
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

            Path thumbnailsDirectory = Paths.get(System.getProperty("user.dir"), "thumbnails");

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

    @PostMapping("/updateProfilePicture/{userId}")
    public String updateProfilePicture(@PathVariable("userName") String userName,
            @RequestParam("file") MultipartFile file) throws IllegalStateException,
            IOException {

        if (file.isEmpty()) {
            throw new RuntimeException("File not Found");
        }
        Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path destination = uploadDir.resolve(UUID.randomUUID().toString());
        File saveFile = new File(destination.toString());
        file.transferTo(saveFile);

        UserDTO userDTOTemp = (UserDTO) userService.getUserByUserName(userName, dtotype.FULL);
        if (!"/sprites/profile_picture.jpg".equals(userDTOTemp.getProfilePhoto())) {
            Files.delete(Paths.get(userDTOTemp.getProfilePhoto()));
        }

        userDTOTemp.setProfilePhoto("/uploads/" + saveFile.getName());
        //userService.save(userDTOTemp);

        return "redirect:/user/login";
    }

}
