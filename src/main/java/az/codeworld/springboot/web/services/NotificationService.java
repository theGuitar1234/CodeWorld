package az.codeworld.springboot.web.services;

import java.util.List;

import az.codeworld.springboot.utilities.constants.notificationtype;
import az.codeworld.springboot.web.entities.Notification;
import az.codeworld.springboot.web.entities.NotificationRecipient;
import az.codeworld.springboot.web.records.NotificationRecord;

public interface NotificationService {

    void saveNotification(Notification notificationId);
    void saveNotificationRecipient(NotificationRecipient notificationRecipient);

    void notify(notificationtype notificationtype, String title, String body, String link, Long userId);

    long countUnread(Long userId);
    long countRead(Long userId);

    List<NotificationRecord> getLatestNotifications(Long userId);
    void markAllRead(Long userId);
    void markRead(Long userId, Long notificationId);
}
