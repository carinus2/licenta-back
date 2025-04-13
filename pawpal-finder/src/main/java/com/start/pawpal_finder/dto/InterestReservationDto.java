package com.start.pawpal_finder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterestReservationDto {

    private Integer id;
    private Integer postId;
    private Integer petOwnerId;
    private Integer petSitterId;
    private String status;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean sitterMarkedComplete;
    private boolean ownerMarkedComplete;

}
