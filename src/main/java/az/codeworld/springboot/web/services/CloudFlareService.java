package az.codeworld.springboot.web.services;

import org.springframework.web.multipart.MultipartFile;

public interface CloudFlareService {
    String uploadImage(MultipartFile file);
    String deliveryUrl(String imageId, String variant);       
}
