package az.codeworld.springboot.web.services.serviceImpl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import az.codeworld.springboot.utilities.configurations.ApplicationProperties;
import az.codeworld.springboot.utilities.constants.notificationtype;
import az.codeworld.springboot.web.entities.Notification;
import az.codeworld.springboot.web.entities.NotificationRecipient;
import az.codeworld.springboot.web.records.NotificationRecord;
import az.codeworld.springboot.web.repositories.NotificationRecipientRepository;
import az.codeworld.springboot.web.repositories.NotificationRepository;
import az.codeworld.springboot.web.services.NotificationService;
import jakarta.transaction.Transactional;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final String DATE_TIME_FORMAT = "dd-MM-yyyy";

    private final ApplicationProperties applicationProperties;
    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;

    public NotificationServiceImpl(
        NotificationRepository notificationRepository,
        NotificationRecipientRepository notificationRecipientRepository
    , ApplicationProperties applicationProperties) {
        this.notificationRepository = notificationRepository;
        this.notificationRecipientRepository = notificationRecipientRepository;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public void saveNotification(Notification notification) {
        if (notification.getCreatedAt() == null) 
            notification.setCreatedAt(Instant.now());

        notificationRepository.save(notification);
        //notificationRepository.flush();
    }

    @Override
    public void saveNotificationRecipient(NotificationRecipient notificationRecipient) {
        if (notificationRecipient.getDeliveredAt() == null) 
            notificationRecipient.setDeliveredAt(Instant.now());

        notificationRecipientRepository.save(notificationRecipient);
        notificationRecipientRepository.flush();
    }

    @Override
    @Transactional
    public void notify(notificationtype notificationtype, String title, String body, String link, Long userId) {

        Notification notification = new Notification();
        notification.setNotificationType(notificationtype);
        notification.setTitle(title);
        notification.setNotificationContent(body);
        notification.setLink(link);

        NotificationRecipient notificationRecipient = new NotificationRecipient();
        notificationRecipient.setDeliveredAt(Instant.now());
        notificationRecipient.setRecipientId(userId);

        notification.addNotificationRecipient(notificationRecipient);

        saveNotification(notification);
        //saveNotificationRecipient(notificationRecipient);
    }

    @Override
    public long countUnread(Long userId) {
        return notificationRecipientRepository.countByRecipientIdAndIsRead(userId, false);
    }

    @Override 
    public long countRead(Long userId) {
        return notificationRecipientRepository.countByRecipientIdAndIsRead(userId, true);
    }

    @Override
    public List<NotificationRecord> getLatestNotifications(Long userId) {
        return notificationRepository.findLatestNotifications(userId)
            .stream()
            .map(n -> new NotificationRecord(n.getId(), n.getNotificationContent(), LocalDateTime.ofInstant(n.getCreatedAt(), ZoneId.of(applicationProperties.getTime().getZone())).format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)), n.getLink()))
            .toList();
    }

    @Override
    public void markAllRead(Long userId) {
        notificationRecipientRepository.markAllRead(userId);
    }

    @Override
    public void markRead(Long userId, Long notificationId) {
        notificationRecipientRepository.markRead(userId, notificationId);
    }
    
}
