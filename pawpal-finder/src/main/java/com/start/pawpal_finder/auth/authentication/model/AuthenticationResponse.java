package com.start.pawpal_finder.auth.authentication.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AuthenticationResponse {

    public String jwt;
    public List<String> roles;

    public AuthenticationResponse(String jwt, List<String> roles) {
        this.jwt = jwt;
        this.roles = roles;
    }

}
