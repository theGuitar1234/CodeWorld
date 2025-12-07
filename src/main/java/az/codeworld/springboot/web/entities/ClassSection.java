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
@Table(name="CLASS_SECTIONS")
public class ClassSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long classId;

    @Column(name = "CLASS_TITLE")
    private String classTitle;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;
}
