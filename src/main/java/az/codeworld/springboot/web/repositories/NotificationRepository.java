package az.codeworld.springboot.web.repositories;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.web.entities.Notification;
import org.springframework.data.repository.query.Param;
import jakarta.transaction.Transactional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findTop5ByNotificationRecipients_RecipientIdAndNotificationRecipients_IsReadFalseOrderByCreatedAtAsc(Long recipientId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoff")
    int deleteNotificationsByCreatedAtBefore(@Param("cutoff") Instant cutoff);
} 
