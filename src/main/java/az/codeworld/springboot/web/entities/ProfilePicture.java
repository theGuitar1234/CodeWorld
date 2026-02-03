package az.codeworld.springboot.web.entities;

import java.time.LocalDateTime;
import java.util.List;

import az.codeworld.springboot.admin.entities.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ProfilePictures")
public class ProfilePicture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    @Column(name = "profile_title")
    private String profileTitle;

    @Column(name = "profile_description")
    private String description;

    @Column(name = "added")
    private LocalDateTime added;

    @Column(name = "photo")
    private String profilePhoto = "/assets/sprites/profile-thumb.jpg";

    @Column
    private String thumbnail;

    @OneToOne
    @JoinColumn(name="userId", nullable = false)
    private User user;

    @PrePersist
    private void onCreate() {
        if (added == null) {
            added = LocalDateTime.now();
        }
        if (profilePhoto == null || profilePhoto.isBlank()) {
            profilePhoto = "/assets/sprites/profile-thumb.jpg";
        }
    }
}