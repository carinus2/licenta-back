package com.start.pawpal_finder.service;

import com.start.pawpal_finder.entity.NotificationEntity;
import com.start.pawpal_finder.dto.NotificationMessageDto;
import com.start.pawpal_finder.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class PersistentNotificationService {

    private final NotificationRepository notificationRepository;
    @Autowired
    public PersistentNotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void saveNotification(NotificationMessageDto notif, Integer id, Integer postId) {
        NotificationEntity notification = new NotificationEntity(
                id,
                notif.getTitle(),
                notif.getMessage(),
                LocalDateTime.now(),
                false
        );
        notification.setReservationId(postId);
        notification.setPostId(postId);
        notificationRepository.save(notification);
    }
}
