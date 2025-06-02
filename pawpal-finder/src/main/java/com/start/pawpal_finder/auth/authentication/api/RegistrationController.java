package com.start.pawpal_finder.auth.authentication.api;

import com.start.pawpal_finder.auth.JwtUtil;
import com.start.pawpal_finder.auth.authentication.model.AuthenticationResponse;
import com.start.pawpal_finder.dto.PetOwnerDto;
import com.start.pawpal_finder.dto.PetSitterDto;
import com.start.pawpal_finder.service.PetOwnerService;
import com.start.pawpal_finder.service.PetSitterService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller pentru înregistrare (register). După ce creează entitatea,
 * autentifică imediat noul cont și returnează token-ul JWT + datele necesare.
 */
@RestController
@RequestMapping("/api/auth")
public class RegistrationController {

    private final PetSitterService petSitterService;
    private final PetOwnerService petOwnerService;
    private final JwtUtil jwtUtils;
    private final AuthenticationManager authenticationManager;

    public RegistrationController(PetSitterService petSitterService,
                                  PetOwnerService petOwnerService,
                                  JwtUtil jwtUtils,
                                  AuthenticationManager authenticationManager) {
        this.petSitterService = petSitterService;
        this.petOwnerService = petOwnerService;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register-pet-sitter")
    public ResponseEntity<?> registerPetSitter(@RequestBody PetSitterDto dto) {
        try {
            // 1. Creăm pet sitter în baza de date
            petSitterService.registerPetSitter(dto);

            // 2. Autentificăm credențialele imediat
            var authToken = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
            var authentication = authenticationManager.authenticate(authToken);

            // 3. Generăm JWT pe baza autentificării (rolurile vin din CustomUserDetailsService)
            String jwt = jwtUtils.generateToken(authentication);

            // 4. Returnăm un DTO cu token, roluri și datele extra pe care le vrei front-end-ului
            return ResponseEntity.ok(new AuthenticationResponse(
                    jwt,
                    List.of("ROLE_PET_SITTER"),
                    dto.getEmail(),
                    dto.getFirstName(),
                    dto.getLastName(),
                    dto.getId()   // presupunem că PetSitterDto conține getId() după save
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/register-pet-owner")
    public ResponseEntity<?> registerPetOwner(@RequestBody PetOwnerDto petOwnerDto) {
        try {
            // 1. Creăm pet owner în baza de date
            petOwnerService.registerPetOwner(petOwnerDto);

            // 2. Autentificăm credențialele imediat
            var authToken = new UsernamePasswordAuthenticationToken(petOwnerDto.getEmail(), petOwnerDto.getPassword());
            var authentication = authenticationManager.authenticate(authToken);

            // 3. Generăm JWT pe baza autentificării
            String jwt = jwtUtils.generateToken(authentication);

            // 4. Returnăm un DTO cu token, roluri și datele extra
            return ResponseEntity.ok(new AuthenticationResponse(
                    jwt,
                    List.of("ROLE_PET_OWNER"),
                    petOwnerDto.getEmail(),
                    petOwnerDto.getFirstName(),
                    petOwnerDto.getLastName(),
                    petOwnerDto.getId()  // presupunem că PetOwnerDto conține getId() după save
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
