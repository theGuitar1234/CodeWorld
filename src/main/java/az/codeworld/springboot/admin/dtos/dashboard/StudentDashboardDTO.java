package az.codeworld.springboot.admin.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDashboardDTO {
    private Long id;
    private String firstName;
    private String lastName;
}
