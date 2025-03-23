package com.start.pawpal_finder.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetSitterProfileDto {
    private Integer id;
    private Integer petSitterId;

    private String firstName;
    private String lastName;
    private String email;
    private String city;
    private String county;
    private String phoneNumber;
    private LocalDate birthDate;


    private String profilePictureUrl;
    private String bio;
    private Boolean notificationsEnabled;
    private String preferredPaymentMethod;
    private Integer experience;

    private List<ReviewDto> reviews;
}
