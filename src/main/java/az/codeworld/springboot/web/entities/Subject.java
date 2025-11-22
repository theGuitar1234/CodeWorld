package az.codeworld.springboot.web.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import az.codeworld.springboot.admin.entities.Student;
import az.codeworld.springboot.admin.entities.Teacher;
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

    @JsonIgnore
    @ManyToMany(mappedBy = "subjects")
    private List<Student> students = new ArrayList<>();

    @ManyToMany(mappedBy = "subjects")
    private List<Teacher> teachers = new ArrayList<>();
}
