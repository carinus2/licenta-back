package com.start.pawpal_finder.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationDto {
    private Long id;
    private Integer sitterId;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private Boolean isRead;

}
