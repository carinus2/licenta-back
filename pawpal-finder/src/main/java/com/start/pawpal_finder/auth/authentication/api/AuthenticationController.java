package com.start.pawpal_finder.auth.authentication.api;

import com.start.pawpal_finder.auth.JwtUtil;
import com.start.pawpal_finder.auth.authentication.model.AuthenticationRequest;
import com.start.pawpal_finder.auth.authentication.model.AuthenticationResponse;
import com.start.pawpal_finder.entity.PetOwnerEntity;
import com.start.pawpal_finder.entity.PetSitterEntity;
import com.start.pawpal_finder.service.PetOwnerService;
import com.start.pawpal_finder.service.PetSitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private PetSitterService petSitterService;

    @Autowired
    private PetOwnerService petOwnerService;

    @Autowired
    private JwtUtil jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest request) {
        PetSitterEntity petSitter = petSitterService.findByEmail(request.getUsername());
        if (petSitter != null && petSitter.getPassword().equals(request.getPassword())) {
            String jwt = jwtUtils.generateJwtToken(request.getUsername());
            return ResponseEntity.ok(new AuthenticationResponse(jwt, Collections.singletonList("ROLE_PET_SITTER")));
        }

        PetOwnerEntity petOwner = petOwnerService.findByEmail(request.getUsername());
        if (petOwner != null && petOwner.getPassword().equals(request.getPassword())) {
            String jwt = jwtUtils.generateJwtToken(request.getUsername());
            return ResponseEntity.ok(new AuthenticationResponse(jwt, Collections.singletonList("ROLE_PET_OWNER")));
        }

        return ResponseEntity.status(401).body("Invalid username or password");
    }
}
