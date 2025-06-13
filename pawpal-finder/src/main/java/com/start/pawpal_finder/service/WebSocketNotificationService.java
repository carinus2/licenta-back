package com.start.pawpal_finder.service;

import com.start.pawpal_finder.dto.NotificationMessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    @Autowired
    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotificationToSitter(NotificationMessageDto notif, Integer sitterId) {
        messagingTemplate.convertAndSend("/topic/sitterNotifications/" + sitterId, notif);
    }

    public void sendNotificationToOwner(NotificationMessageDto notif) {
        messagingTemplate.convertAndSend("/topic/ownerNotifications/", notif);
    }
}
