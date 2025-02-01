package com.start.pawpal_finder.controller;

import com.start.pawpal_finder.service.PetSitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pet-sitter")
public class PetSitterController {
    @Autowired
    private PetSitterService petSitterService;

    @GetMapping("/has-animals")
    public ResponseEntity<Boolean> hasAnimals(@RequestParam String email) {
        return ResponseEntity.ok(petSitterService.hasAnimals(email));
    }
}
