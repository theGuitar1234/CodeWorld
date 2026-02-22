package az.codeworld.springboot.web.entities;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="SUBJECTS")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SUBJECT_TITLE", unique = true)
    private String subjectTitle;

    @Column(name = "SUBJECT_BODY")
    private String subjectBody;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.MERGE, orphanRemoval = true)
    private List<ClassSection> classSections = new ArrayList<>();

    @OneToMany(mappedBy = "subject", cascade = CascadeType.MERGE, orphanRemoval = true)
    private List<SubjectEnrollment> subjectEnrollments = new ArrayList<>();

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseOffering> courseOfferings = new ArrayList<>();
}
