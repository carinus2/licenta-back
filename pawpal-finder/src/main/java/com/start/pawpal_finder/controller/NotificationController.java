package com.start.pawpal_finder.controller;

import com.start.pawpal_finder.entity.NotificationEntity;
import com.start.pawpal_finder.repository.NotificationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/{sitterId}")
    public List<NotificationEntity> getNotificationsForSitter(@PathVariable Integer sitterId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(sitterId);
    }
    @GetMapping("/owner/{ownerId}")
    public List<NotificationEntity> getNotificationsForOwner(@PathVariable Integer ownerId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(ownerId);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));
        notification.setIsRead(true);
        notificationRepository.save(notification);
        return ResponseEntity.ok().build();
    }

}
