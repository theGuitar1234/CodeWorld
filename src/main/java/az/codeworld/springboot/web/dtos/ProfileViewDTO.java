package az.codeworld.springboot.web.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileViewDTO {
    private Long profileId;
}
