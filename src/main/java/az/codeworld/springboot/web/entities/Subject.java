package az.codeworld.springboot.web.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
// @Builder
@ToString
@NoArgsConstructor
@Table(name="SUBJECTS")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long subjectId;

    @Column(name = "SUBJECT_TITLE")
    private String subjectTitleString;

    @Column(name = "SUBJECT_BODY")
    private String subjectBodyString;

    @OneToMany
    private List<ClassSection> classSections = new ArrayList<>();
}
