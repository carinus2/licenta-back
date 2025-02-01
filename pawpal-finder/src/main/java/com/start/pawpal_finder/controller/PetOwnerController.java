package com.start.pawpal_finder.controller;

import com.start.pawpal_finder.service.PetOwnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pet-owner")
public class PetOwnerController {

    private final PetOwnerService petOwnerService;

    public PetOwnerController(PetOwnerService petOwnerService) {
        this.petOwnerService = petOwnerService;
    }
    @GetMapping("/has-animals")
    public ResponseEntity<Boolean> hasAnimals(@RequestParam String email) {
        return ResponseEntity.ok(petOwnerService.hasAnimals(email));
    }

}
