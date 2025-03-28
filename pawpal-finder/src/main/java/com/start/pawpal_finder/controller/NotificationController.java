package com.start.pawpal_finder.controller;

import com.start.pawpal_finder.entity.NotificationEntity;
import com.start.pawpal_finder.repository.NotificationRepository;
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
        return notificationRepository.findBySitterIdOrderByCreatedAtDesc(sitterId);
    }

}
