package com.start.pawpal_finder.auth.authentication.model;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    public String jwt;
    public List<String> roles;
    public String email;
    public String firstName;
    public String lastName;
    public Integer userId;

    public AuthenticationResponse(String jwt, List<String> roles, String email) {
        this.jwt = jwt;
        this.roles = roles;
        this.email = email;
    }

    public AuthenticationResponse(String jwt, List<String> roles) {
        this.jwt = jwt;
        this.roles = roles;
    }
}
