package com.start.pawpal_finder.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlotDto {

    private Long id;
    private String startTime;
    private String endTime;
    private String status;
    private Integer sitterId;
    private String date;
}
