package com.start.pawpal_finder.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.start.pawpal_finder.dto.PetSitterProfileDto;
import com.start.pawpal_finder.dto.ReviewDto;
import com.start.pawpal_finder.service.PetSitterProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/pet-sitter-profile")
public class PetSitterProfileController {

    private final PetSitterProfileService profileService;

    public PetSitterProfileController(PetSitterProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{sitterId}")
    public ResponseEntity<PetSitterProfileDto> getProfile(@PathVariable Integer sitterId) {
        return profileService.getProfileBySitterId(sitterId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PetSitterProfileDto> saveProfile(
            @RequestPart("profileData") String profileDataJson,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture
    ) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        PetSitterProfileDto dto = mapper.readValue(profileDataJson, PetSitterProfileDto.class);

        PetSitterProfileDto updatedProfile = profileService.saveProfile(dto, profilePicture);
        return ResponseEntity.ok(updatedProfile);
    }

    @DeleteMapping("/{sitterId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Integer sitterId) {
        profileService.deleteProfile(sitterId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<PetSitterProfileDto> searchSittersByLocation(@RequestParam String city, @RequestParam String county) {
        return profileService.findByCityAndCounty(city, county);
    }

    @GetMapping("/profile/{sitterId}/reviews")
    public ResponseEntity<List<ReviewDto>> getReviews(@PathVariable Integer sitterId,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "3") int size) {
        List<ReviewDto> reviews = profileService.getReviewsForSitter(sitterId, page, size);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/near")
    public List<PetSitterProfileDto> getSittersNear(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(name = "radius", defaultValue = "10000") double radius
    ) {
        return profileService.findSittersNear(lat, lng, radius);
    }

}
