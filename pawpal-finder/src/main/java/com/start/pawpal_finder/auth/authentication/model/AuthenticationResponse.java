package com.start.pawpal_finder.auth.authentication.model;

import com.start.pawpal_finder.auth.JwtUtil;
import com.start.pawpal_finder.dto.CustomUserDetails;
import lombok.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

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

    public AuthenticationResponse(String jwt, List<String> roles, String email, Integer userId) {
        this.jwt = jwt;
        this.roles = roles;
        this.email = email;
        this.userId = userId;
    }

    public AuthenticationResponse(String jwt, List<String> roles) {
        this.jwt = jwt;
        this.roles = roles;
    }

    public static AuthenticationResponse fromAuthentication(
            Authentication authentication, JwtUtil jwtUtils) {

        String token = jwtUtils.generateToken(authentication);

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            return new AuthenticationResponse(token, List.of());
        }

        CustomUserDetails userDetails = (CustomUserDetails) principal;

        List<String> rolesList = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String email = userDetails.getUsername();
        Integer id = userDetails.getId();

        AuthenticationResponse response = new AuthenticationResponse();
        response.setJwt(token);
        response.setRoles(rolesList);
        response.setEmail(email);
        response.setUserId(id);
        return response;
    }
}
