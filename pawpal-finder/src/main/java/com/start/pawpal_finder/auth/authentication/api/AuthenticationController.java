package com.start.pawpal_finder.auth.authentication.api;
import org.springframework.security.core.GrantedAuthority;
import java.util.stream.Collectors;
import java.util.List;
import com.start.pawpal_finder.auth.JwtUtil;
import com.start.pawpal_finder.auth.authentication.model.AuthenticationRequest;
import com.start.pawpal_finder.auth.authentication.model.AuthenticationResponse;
import com.start.pawpal_finder.dto.CustomUserDetails;
import com.start.pawpal_finder.service.CustomUserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final JwtUtil jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationController(JwtUtil jwtUtils,
                                    CustomUserDetailsService customUserDetailsService,
                                    AuthenticationManager authenticationManager) {
        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authRequest) {
        try {
            // 1) Authenticate credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );

            // 2) Generate the JWT
            String jwt = jwtUtils.generateToken(authentication);

            // 3) Extract the CustomUserDetails principal (which now carries an 'id')
            Object principal = authentication.getPrincipal();
            if (!(principal instanceof CustomUserDetails)) {
                // Should not happen in normal flow. Just in case, we return a minimal response:
                return ResponseEntity.ok(new AuthenticationResponse(jwt, null));
            }

            CustomUserDetails userDetails = (CustomUserDetails) principal;

            // 4) Extract each piece:
            String email = userDetails.getUsername();
            List<String> rolesList = userDetails.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            Integer userId = userDetails.getId(); // ← this is the numeric DB ID

            // 5) Construct the response in the exact order: (String jwt, List<String> roles, String email, Integer userId)
            AuthenticationResponse response = new AuthenticationResponse(
                    jwt,
                    rolesList,
                    email,
                    userId
            );

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid authentication values");
        }
    }
}
