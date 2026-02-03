package az.codeworld.springboot.web.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.web.entities.ProfilePicture;

@Repository
public interface ProfilePictureRepository extends JpaRepository<ProfilePicture, Long> {
    Optional<ProfilePicture> findByUser(User user);
    Optional<ProfilePicture> findByUser_UserName(String userName);
}
