package com.start.pawpal_finder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimalDto {
    private Integer id;
    private String name;
    private String street;
    private String description;
    private Integer age;
    private String breed;
    private String profilePicture;
    private Integer petOwnerId;
}
