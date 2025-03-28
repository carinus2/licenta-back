package com.start.pawpal_finder.service;

import com.start.pawpal_finder.entity.NotificationEntity;
import com.start.pawpal_finder.dto.NotificationMessageDto;
import com.start.pawpal_finder.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class PersistentNotificationService {

    private final NotificationRepository notificationRepository;

    public PersistentNotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public NotificationEntity saveNotification(NotificationMessageDto notif, Integer sitterId) {
        NotificationEntity notification = new NotificationEntity(
                sitterId,
                notif.getTitle(),
                notif.getMessage(),
                LocalDateTime.now(),
                false
        );
        return notificationRepository.save(notification);
    }
}
