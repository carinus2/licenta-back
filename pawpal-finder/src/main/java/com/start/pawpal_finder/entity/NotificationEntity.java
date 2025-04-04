package com.start.pawpal_finder.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "notifications")
public class NotificationEntity {

    @Id
    @SequenceGenerator(name = "notificationGenerator", sequenceName = "sq_notification_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Integer userId;

    private String title;

    @Column(length = 1000)
    private String message;

    private LocalDateTime createdAt;

    private Boolean isRead = false;

    public NotificationEntity(Integer userId, String title, String message, LocalDateTime createdAt, Boolean isRead) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

}
