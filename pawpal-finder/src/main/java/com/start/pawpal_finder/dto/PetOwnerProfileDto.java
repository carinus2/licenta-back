package com.start.pawpal_finder.dto;

import com.start.pawpal_finder.entity.PetOwnerEntity;
import com.start.pawpal_finder.entity.PetOwnerProfileEntity;
import lombok.*;

import java.math.BigDecimal;
import java.util.Arrays;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetOwnerProfileDto {
    private Integer id;
    private Integer petOwnerId;

    private String firstName;
    private String lastName;
    private String email;
    private String city;
    private String county;
    private String phoneNumber;

    private String profilePictureUrl;
    private BigDecimal budget;
    private String bio;
    private Boolean notificationsEnabled;
    private String preferredPaymentMethod;

    private String street;
    private String streetNumber;
    private Double latitude;
    private Double longitude;
    public PetOwnerProfileDto(PetOwnerEntity petOwner, PetOwnerProfileEntity profile) {
        this.petOwnerId = petOwner.getId();
        this.firstName = petOwner.getFirstName();
        this.lastName = petOwner.getLastName();
        this.email = petOwner.getEmail();
        this.city = petOwner.getCity();
        this.county = petOwner.getCounty();
        this.phoneNumber = petOwner.getPhoneNumber();
        this.profilePictureUrl = Arrays.toString(profile.getProfilePictureUrl());
        this.budget = profile.getBudget();
        this.bio = profile.getBio();
        this.notificationsEnabled = profile.getNotificationsEnabled();
        this.preferredPaymentMethod = profile.getPreferredPaymentMethod();
        this.street = profile.getStreet();
        this.streetNumber = profile.getStreetNumber();
        this.latitude = profile.getLatitude();
        this.longitude = profile.getLongitude();
    }
}
