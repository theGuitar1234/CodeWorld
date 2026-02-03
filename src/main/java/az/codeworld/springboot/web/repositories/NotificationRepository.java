package az.codeworld.springboot.web.repositories;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.web.entities.Notification;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    @Query("""
        SELECT n FROM Notification n 
        JOIN n.notificationRecipients nr 
        WHERE nr.recipientId = :recipientId
        AND nr.isRead = false
        ORDER BY n.createdAt ASC LIMIT 5
    """)
    List<Notification> findLatestNotifications(@Param("recipientId") Long recipientId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoff")
    int deleteNotificationsByCreatedAtBefore(@Param("cutoff") Instant cutoff);
} 
