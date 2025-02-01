package com.start.pawpal_finder.auth.authentication.api;

import com.start.pawpal_finder.auth.JwtUtil;
import com.start.pawpal_finder.auth.authentication.model.AuthenticationRequest;
import com.start.pawpal_finder.auth.authentication.model.AuthenticationResponse;
import com.start.pawpal_finder.service.PetOwnerService;
import com.start.pawpal_finder.service.PetSitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private JwtUtil jwtUtils;

    @Autowired
    private PetOwnerService petOwnerService;

    @Autowired
    private PetSitterService petSitterService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationController(JwtUtil jwtUtils, AuthenticationManager authenticationManager) {
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return ResponseEntity.ok(new AuthenticationResponse(null, List.of("ROLE_USER"), userDetails.getUsername(), null, null));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticateUser(@RequestBody AuthenticationRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        if ("admin@email.com".equals(request.getEmail()) && "admin1234".equals(request.getPassword())) {
            var jwt = jwtUtils.generateAdminJwtToken(authentication);
            return ResponseEntity.ok(new AuthenticationResponse(jwt, List.of("ROLE_ADMIN"), "Admin", null, null));
        } else {
            var petOwner = petOwnerService.findByEmail(request.getEmail());
            if (petOwner.isPresent()) {
                var owner = petOwner.get();
                var jwt = jwtUtils.generateJwtToken(authentication);
                return ResponseEntity.ok(new AuthenticationResponse(jwt, List.of("ROLE_USER"), owner.getEmail(), owner.getFirstName(), owner.getLastName()));
            }

            var petSitter = petSitterService.findByEmail(request.getEmail());
            if (petSitter.isPresent()) {
                var sitter = petSitter.get();
                var jwt = jwtUtils.generateJwtToken(authentication);
                return ResponseEntity.ok(new AuthenticationResponse(jwt, List.of("ROLE_USER"), sitter.getEmail(), sitter.getFirstName(), sitter.getLastName()));
            }

            return ResponseEntity.status(401).body(null);
        }
    }
}
