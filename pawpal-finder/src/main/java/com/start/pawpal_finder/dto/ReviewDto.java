package com.start.pawpal_finder.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Integer id;
    private Integer reservationId;
    private String content;
    private int rating;
    private String createdAt;
    private String writtenByRole;
    private Integer writtenById;
    private String reviewedRole;
    private Integer reviewedId;
}
