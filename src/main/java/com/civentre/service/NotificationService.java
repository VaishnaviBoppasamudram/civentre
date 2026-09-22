package com.civentre.service;

import com.civentre.entity.Issue;
import com.civentre.entity.Notification;
import com.civentre.entity.User;
import com.civentre.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void createNotification(
            User user,
            Issue issue,
            String message,
            String type) {

        Notification notification =
                new Notification(user, issue, message, type);

        notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(User user) {

        return notificationRepository
                .findByUserOrderByCreatedAtDesc(user);
    }

    public long getUnreadCount(User user) {

        return notificationRepository
                .countByUserAndReadFalse(user);
    }

    public void markAsRead(Long notificationId, User user) {

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() ->
                                new RuntimeException("Notification not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to update this notification");
        }

        notification.setRead(true);

        notificationRepository.save(notification);
    }
}