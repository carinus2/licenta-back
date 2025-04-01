package com.start.pawpal_finder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {
    private Integer id;
    private Integer postSitterId;
    private Integer petOwnerId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double finalPrice;

    // New fields:
    private String postDescription;
    private String availabilityStart;
    private String availabilityEnd;
    private String petSitterName;

    private String petOwnerName;
}
