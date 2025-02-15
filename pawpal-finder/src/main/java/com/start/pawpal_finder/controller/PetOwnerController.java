package com.start.pawpal_finder.controller;

import com.start.pawpal_finder.service.AnimalService;
import com.start.pawpal_finder.service.PetOwnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pet-owner")
public class PetOwnerController {

    private final PetOwnerService petOwnerService;
    private final AnimalService animalService;
    public PetOwnerController(PetOwnerService petOwnerService, AnimalService animalService) {
        this.petOwnerService = petOwnerService;
        this.animalService = animalService;
    }

    @GetMapping("/has-animals")
    public ResponseEntity<Boolean> hasAnimals(@RequestParam String email) {
        return ResponseEntity.ok(petOwnerService.hasAnimals(email));
    }

    @GetMapping("/number-of-animals")
    public ResponseEntity<Integer> getNumberOfAnimals(@RequestParam String email) {
        int count = animalService.countAnimalsByOwnerEmail(email);
        return ResponseEntity.ok(count);
    }
}