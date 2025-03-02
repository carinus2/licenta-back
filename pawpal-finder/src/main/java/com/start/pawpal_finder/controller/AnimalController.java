package com.start.pawpal_finder.controller;

import com.start.pawpal_finder.dto.AnimalDto;
import com.start.pawpal_finder.service.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/animals")
public class AnimalController {

    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AnimalDto> saveAnimal(
            @RequestParam Integer ownerId,
            @RequestPart("animalDto") AnimalDto animalDto,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture
    ) {
        AnimalDto savedAnimal = animalService.saveAnimal(ownerId, animalDto, profilePicture);
        return ResponseEntity.ok(savedAnimal);
    }

    @PutMapping(value = "/{animalId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AnimalDto updateAnimal(@PathVariable Integer animalId,
                                  @RequestPart("animalDto") AnimalDto animalDto,
                                  @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture) {
        return animalService.updateAnimal(animalId, animalDto, profilePicture);
    }


    @DeleteMapping("/{animalId}")
    public void deleteAnimal(@PathVariable Integer animalId) {
        animalService.deleteAnimal(animalId);
    }

    @GetMapping("/count/{ownerId}")
    public ResponseEntity<Long> getAnimalCount(@PathVariable Integer ownerId) {
        Long count = animalService.countAnimalsByOwnerId(ownerId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<AnimalDto>> getAnimalsByOwner(@RequestParam Integer petOwnerId) {
        List<AnimalDto> animals = animalService.getAnimalsByOwner(petOwnerId);
        return ResponseEntity.ok(animals);
    }
}
