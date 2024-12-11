package com.start.pawpal_finder.auth.authentication.api;

import com.start.pawpal_finder.auth.JwtUtil;
import com.start.pawpal_finder.auth.authentication.model.AuthenticationRequest;
import com.start.pawpal_finder.auth.authentication.model.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private JwtUtil jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthenticationController( JwtUtil jwtUtils, AuthenticationManager authenticationManager) {

        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticateUser(@RequestBody AuthenticationRequest request) {

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        if ("admin@email.com".equals(request.getEmail()) && "admin1234".equals(request.getPassword())) {
            var jwt = jwtUtils.generateAdminJwtToken(authentication);
            return ResponseEntity.ok(new AuthenticationResponse(jwt, List.of("ROLE_ADMIN")));
        } else {
            var jwt = jwtUtils.generateJwtToken(authentication);
            return ResponseEntity.ok(new AuthenticationResponse(jwt, List.of("ROLE_USER")));
        }

    }


}
