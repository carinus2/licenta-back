package com.start.pawpal_finder.auth.authentication.api;

import com.start.pawpal_finder.auth.JwtUtil;
import com.start.pawpal_finder.auth.authentication.model.AuthenticationRequest;
import com.start.pawpal_finder.auth.authentication.model.AuthenticationResponse;
import com.start.pawpal_finder.service.PetOwnerService;
import com.start.pawpal_finder.service.PetSitterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final JwtUtil jwtUtils;
    private final PetOwnerService petOwnerService;
    private final PetSitterService petSitterService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationController(JwtUtil jwtUtils, PetOwnerService petOwnerService, PetSitterService petSitterService, AuthenticationManager authenticationManager) {
        this.jwtUtils = jwtUtils;
        this.petOwnerService = petOwnerService;
        this.petSitterService = petSitterService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticateUser(
            @RequestBody AuthenticationRequest request) {

        // 1) authenticate credentials
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );

        // 2) lookup as Pet-Owner first
        var petOwnerOpt = petOwnerService.findByEmail(request.getEmail());
        if (petOwnerOpt.isPresent() && Boolean.TRUE.equals(petOwnerOpt.get().getAdmin())) {
            // 2a) PET_OWNER + ADMIN
            var owner = petOwnerOpt.get();
            var jwt   = jwtUtils.generateAdminJwtToken(authentication);
            return ResponseEntity.ok(new AuthenticationResponse(
                    jwt,
                    List.of("ROLE_ADMIN"),
                    owner.getEmail(),
                    owner.getFirstName(),
                    owner.getLastName(),
                    owner.getId()
            ));
        }
        else if (petOwnerOpt.isPresent()) {
            // 2b) PET_OWNER only
            var owner = petOwnerOpt.get();
            var jwt   = jwtUtils.generateJwtToken(authentication);
            return ResponseEntity.ok(new AuthenticationResponse(
                    jwt,
                    List.of("ROLE_PET_OWNER"),
                    owner.getEmail(),
                    owner.getFirstName(),
                    owner.getLastName(),
                    owner.getId()
            ));
        }

        // 3) if not a Pet-Owner at all, try Pet-Sitter
        var sitterOpt = petSitterService.findByEmail(request.getEmail());
        if (sitterOpt.isPresent()) {
            var sitter = sitterOpt.get();
            var jwt    = jwtUtils.generateJwtToken(authentication);
            return ResponseEntity.ok(new AuthenticationResponse(
                    jwt,
                    List.of("ROLE_PET_SITTER"),
                    sitter.getEmail(),
                    sitter.getFirstName(),
                    sitter.getLastName(),
                    sitter.getId()
            ));
        }

        // 4) fallback
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


}
