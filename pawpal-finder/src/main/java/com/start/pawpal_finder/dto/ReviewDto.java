package com.start.pawpal_finder.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Integer id;
    private Integer rating;
    private String content;
}
