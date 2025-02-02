package com.start.pawpal_finder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetOwnerDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String city;
    private String county;
    private String phoneNumber;
    private Boolean admin;
}

