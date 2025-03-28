package com.start.pawpal_finder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

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
}
