package az.codeworld.springboot.web.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfilePayloadDTO {
    
    @NotBlank
    @Schema(
        description = "This is the Title of the Album", 
        example = "title",
        requiredMode = RequiredMode.REQUIRED
    )
    private String profileTitle;
    
    @NotBlank
    @Schema(
        description = "This is the Description of the Album", 
        example ="description", 
        requiredMode = RequiredMode.NOT_REQUIRED
    )
    private String description;
}
