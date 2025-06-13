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


    public NotificationEntity saveNotification(NotificationMessageDto notif, Integer sitterId, Integer postId) {
        NotificationEntity notification = new NotificationEntity(
                sitterId,
                notif.getTitle(),
                notif.getMessage(),
                LocalDateTime.now(),
                false
        );
        notification.setReservationId(postId);
        notification.setPostId(postId);
        return notificationRepository.save(notification);
    }

    public NotificationEntity saveOwnerNotification(NotificationMessageDto notif, Integer ownerId, Integer postId) {
        NotificationEntity notification = new NotificationEntity(
                ownerId,
                notif.getTitle(),
                notif.getMessage(),
                LocalDateTime.now(),
                false
        );
        notification.setReservationId(postId);
        notification.setPostId(postId);
        return notificationRepository.save(notification);
    }
}
