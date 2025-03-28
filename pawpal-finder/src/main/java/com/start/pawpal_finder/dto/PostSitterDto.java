package com.start.pawpal_finder.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostSitterDto {
    private Integer id;
    private Integer petSitterId;
    private String description;
    private String status;
    private LocalDate postDate;
    private List<String> tasks;
    private List<PostSitterAvailabilityDto> availability;
    private String pricingModel;
    private Double ratePerHour;
    private Double ratePerDay;
    private Double flatRate;
}
