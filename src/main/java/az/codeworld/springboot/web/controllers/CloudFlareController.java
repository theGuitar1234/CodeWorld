package az.codeworld.springboot.web.controllers;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.utilities.configurations.CloudFlareProperties;
import az.codeworld.springboot.web.clients.CloudFlareImagesClient;

@RestController
@RequestMapping("/api/me")
public class CloudFlareController {

    private final CloudFlareImagesClient imagesClient;
    private final UserService userService;
    private final CloudFlareProperties props;

    public CloudFlareController(CloudFlareImagesClient imagesClient, UserService userService, CloudFlareProperties props) {
        this.imagesClient = imagesClient;
        this.userService = userService;
        this.props = props;
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadAvatar(@RequestPart("file") MultipartFile file, Authentication auth) {
        String imageId = imagesClient.uploadImage(file);

        userService.updateProfileImageId(auth.getName(), imageId);

        return imagesClient.deliveryUrl(imageId, props.getDefaultVariant());
    }

    @GetMapping("/avatar-url")
    public String getAvatarUrl(Authentication auth) {
        String imageId = userService.getProfileImageId(auth.getName());
        if (imageId == null) return null;
        return imagesClient.deliveryUrl(imageId, props.getDefaultVariant());
    }
}
