package com.civentre.controller;

import com.civentre.entity.Notification;
import com.civentre.entity.User;
import com.civentre.repository.UserRepository;
import com.civentre.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationController(
            NotificationService notificationService,
            UserRepository userRepository) {

        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    // ==========================================
    // GET USER NOTIFICATIONS
    // ==========================================

    @GetMapping
    public List<Notification> getNotifications(
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return notificationService.getUserNotifications(user);
    }

    // ==========================================
    // GET UNREAD NOTIFICATION COUNT
    // ==========================================

    @GetMapping("/unread-count")
    public Map<String, Long> getUnreadCount(
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        long count =
                notificationService.getUnreadCount(user);

        return Map.of("count", count);
    }

    // ==========================================
    // MARK NOTIFICATION AS READ
    // ==========================================

    @PutMapping("/{notificationId}/read")
    public Map<String, String> markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        notificationService.markAsRead(
                notificationId,
                user
        );

        return Map.of(
                "message",
                "Notification marked as read"
        );
    }

    // ==========================================
    // GET AUTHENTICATED USER
    // ==========================================

    private User getAuthenticatedUser(
            Authentication authentication) {

        if (authentication == null ||
                authentication.getName() == null ||
                authentication.getName().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication is required"
            );
        }

        return userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Authenticated user was not found"
                        )
                );
    }
}