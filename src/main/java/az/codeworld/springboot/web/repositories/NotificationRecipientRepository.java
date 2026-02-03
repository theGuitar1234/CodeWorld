package az.codeworld.springboot.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.web.entities.NotificationRecipient;
import jakarta.transaction.Transactional;

@Repository
public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, Long> {
    long countByRecipientIdAndIsRead(Long recipientId, boolean isRead);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
        UPDATE NotificationRecipient n
        SET n.isRead = true
        WHERE n.recipientId = :recipientId
    """)
    void markAllRead(@Param("recipientId") Long recipientId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
        UPDATE NotificationRecipient n
        SET n.isRead = true
        WHERE n.recipientId = :recipientId AND n.notification.id = :notificationId
    """)
    void markRead(@Param("recipientId") Long recipientId, @Param("notificationId") Long notificationId);
}
