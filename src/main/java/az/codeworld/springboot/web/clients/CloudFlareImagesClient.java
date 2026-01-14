package az.codeworld.springboot.web.clients;

import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import az.codeworld.springboot.utilities.configurations.CloudFlareProperties;
import az.codeworld.springboot.web.services.CloudFlareService;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class CloudFlareImagesClient implements CloudFlareService {

    private final WebClient webClient;
    private final CloudFlareProperties props;

    public CloudFlareImagesClient(CloudFlareProperties props) {
        this.props = props;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.cloudflare.com/client/v4")
                .defaultHeader("Authorization", "Bearer " + props.getApiToken())
                .build();
    }

    public String uploadImage(MultipartFile file) {
        if (file.isEmpty())
            throw new IllegalArgumentException("Empty file");
        if (file.getSize() > 10 * 1024 * 1024)
            throw new IllegalArgumentException("Max 10MB for Cloudflare Images upload");
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image uploads are allowed");
        }

        MultipartBodyBuilder body = new MultipartBodyBuilder();
        body.part("file", file.getResource()); 
        body.part("requireSignedURLs", "false");
        body.part("metadata", "{\"type\":\"avatar\"}");

        Map<?, ?> resp = webClient.post()
                .uri("/accounts/{accountId}/images/v1", props.getAccountId())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(body.build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (resp == null || Boolean.FALSE.equals(resp.get("success"))) {
            throw new RuntimeException("Cloudflare upload failed: " + resp);
        }

        Map<?, ?> result = (Map<?, ?>) resp.get("result");
        return (String) result.get("id");
    }

    public String deliveryUrl(String imageId, String variant) {
        // https://imagedelivery.net/<ACCOUNT_HASH>/<IMAGE_ID>/<VARIANT>:contentReference[oaicite:8]{index=8}
        return "https://imagedelivery.net/" + props.getAccountHash() + "/" + imageId + "/" + variant;
    }
}
