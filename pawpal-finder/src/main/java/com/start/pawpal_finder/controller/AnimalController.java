package com.start.pawpal_finder.controller;

import com.start.pawpal_finder.dto.AnimalDto;
import com.start.pawpal_finder.service.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/animals")
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    @PostMapping("/save")
    public ResponseEntity<AnimalDto> saveAnimal(@RequestParam Integer ownerId, @RequestBody AnimalDto animalDto) {
        AnimalDto savedAnimal = animalService.saveAnimal(ownerId, animalDto);
        return ResponseEntity.ok(savedAnimal);
    }

    @GetMapping("/count/{ownerId}")
    public ResponseEntity<Long> getAnimalCount(@PathVariable Integer ownerId) {
        Long count = animalService.countAnimalsByOwnerId(ownerId);
        return ResponseEntity.ok(count);
    }
}
