package com.start.pawpal_finder.auth.authentication.api;

import com.start.pawpal_finder.dto.PetOwnerDto;
import com.start.pawpal_finder.dto.PetSitterDto;
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

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class RegistrationController {

    @Autowired
    private PetSitterService petSitterService;

    @Autowired
    private PetOwnerService petOwnerService;


    @PostMapping("/register-pet-sitter")
    public ResponseEntity<?> registerPetSitter(@RequestBody PetSitterDto petSitterDto) {
        try {
            petSitterService.registerPetSitter(petSitterDto);
            return ResponseEntity.ok().body(Map.of("message", "Pet Sitter registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/register-pet-owner")
    public ResponseEntity<?> registerPetOwner(@RequestBody PetOwnerDto petOwnerDto) {
        try {
            petOwnerService.registerPetOwner(petOwnerDto);
            return ResponseEntity.ok().body(Map.of("message", "Pet Owner registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
