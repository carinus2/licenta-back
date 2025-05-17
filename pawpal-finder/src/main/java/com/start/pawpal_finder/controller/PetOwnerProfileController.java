package com.start.pawpal_finder.controller;

import com.start.pawpal_finder.dto.PetOwnerProfileDto;
import com.start.pawpal_finder.dto.ReviewDto;
import com.start.pawpal_finder.service.PetOwnerProfileService;
import org.springframework.context.annotation.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PetOwnerProfileController {

    private final PetOwnerProfileService profileService;

    public PetOwnerProfileController(PetOwnerProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping(value = "/profile", consumes = "multipart/form-data")
    public ResponseEntity<PetOwnerProfileDto> saveProfile(
            @RequestPart("profileData") PetOwnerProfileDto profileData,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture
    ) {
        PetOwnerProfileDto savedProfile = profileService.saveProfile(profileData, profilePicture);
        return ResponseEntity.ok(savedProfile);
    }

    @GetMapping("/all-profiles")
    public ResponseEntity<List<PetOwnerProfileDto>> getAllProfiles(){

        return ResponseEntity.ok(profileService.getAllProfiles());
    }

    @DeleteMapping("/{ownerId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Integer ownerId) {
        profileService.deleteProfile(ownerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile/{ownerId}")
    public ResponseEntity<Optional<PetOwnerProfileDto>> getProfile(@PathVariable Integer ownerId) {
        Optional<PetOwnerProfileDto> profile = profileService.getProfileByOwnerId(ownerId);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/profile/{ownerId}/reviews")
    public ResponseEntity<List<ReviewDto>> getReviews(@PathVariable Integer ownerId,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "3") int size) {
        List<ReviewDto> reviews = profileService.getReviewsForOwner(ownerId, page, size);
        return ResponseEntity.ok(reviews);
    }
}
