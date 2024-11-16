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
            PetSitterEntity petSitter = new PetSitterEntity();
            petSitter.setFirstName(petSitterDto.getFirstName());
            petSitter.setLastName(petSitterDto.getLastName());
            petSitter.setEmail(petSitterDto.getEmail());
            petSitter.setAddress(petSitterDto.getAddress());
            petSitter.setPhoneNumber(petSitterDto.getPhoneNumber());
            petSitter.setBirthDate(petSitterDto.getBirthDate());
            petSitter.setPassword(petSitterDto.getPassword());
            petSitter.setAdmin(false);

            petSitterService.registerPetSitter(petSitter);
            return ResponseEntity.ok("Pet Sitter registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/register-pet-owner")
    public ResponseEntity<?> registerPetOwner(@RequestBody PetOwnerDto petOwnerDto) {
        try {
            PetOwnerEntity petOwner = new PetOwnerEntity();
            petOwner.setFirstName(petOwnerDto.getFirstName());
            petOwner.setLastName(petOwnerDto.getLastName());
            petOwner.setEmail(petOwnerDto.getEmail());
            petOwner.setAddress(petOwnerDto.getAddress());
            petOwner.setPhoneNumber(petOwnerDto.getPhoneNumber());
            petOwner.setPassword(petOwnerDto.getPassword());
            petOwner.setAdmin(false);

            petOwnerService.registerPetOwner(petOwner);
            return ResponseEntity.ok("Pet Owner registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
