package com.start.pawpal_finder.controller;

import com.start.pawpal_finder.dto.InterestReservationDto;
import com.start.pawpal_finder.dto.ReviewDto;
import com.start.pawpal_finder.service.InterestReservationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interest-reservations")
@CrossOrigin(origins = "*")
public class InterestReservationController {

    private final InterestReservationService interestReservationService;

    public InterestReservationController(InterestReservationService interestReservationService) {
        this.interestReservationService = interestReservationService;
    }

    @PostMapping
    public ResponseEntity<InterestReservationDto> createInterestReservation(
            @RequestParam Integer postId,
            @RequestParam Integer petSitterId,
            @RequestParam(required = false) String message) {
        InterestReservationDto dto = interestReservationService.createInterestReservation(postId, petSitterId, message);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{interestId}/complete")
    public ResponseEntity<InterestReservationDto> markInterestCompleted(
            @PathVariable Integer interestId,
            @RequestParam String role) {
        InterestReservationDto dto = interestReservationService.markInterestCompleted(interestId, role);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/get-by-post")
    public ResponseEntity<InterestReservationDto> getForPostAndSitter(
            @RequestParam Integer postId,
            @RequestParam Integer sitterId) {
        return interestReservationService.getInterestByPostAndSitterId(postId, sitterId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/get-by-post-owner")
    public ResponseEntity<InterestReservationDto> getForPostAndOwner(
            @RequestParam Integer postId,
            @RequestParam Integer ownerId) {
        return ResponseEntity.ok(
                interestReservationService.getInterestByPostAndOwnerId(postId, ownerId)
        );
    }

    @PostMapping("/{interestReservationId}/review-interest")
    public ResponseEntity<ReviewDto> submitReview(
            @PathVariable Integer interestReservationId,
            @RequestBody ReviewDto reviewDto) {
        ReviewDto savedReview = interestReservationService.submitReviewOnInterest(interestReservationId, reviewDto);
        return ResponseEntity.ok(savedReview);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<Page<InterestReservationDto>> getInterestReservationsForOwner(
            @PathVariable Integer ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<InterestReservationDto> dtos = interestReservationService.getInterestReservationsForOwner(ownerId, page, size);
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<InterestReservationDto> updateStatus(
            @PathVariable Integer id,
            @RequestParam String status,
            @RequestParam String ownerName
    ) {
        return ResponseEntity.ok(interestReservationService.updateStatus(id, status, ownerName));
    }

    @GetMapping("/sitter/{sitterId}")
    public ResponseEntity<List<InterestReservationDto>> getBySitter(
            @PathVariable Integer sitterId
    ) {
        List<InterestReservationDto> list =
                interestReservationService.getInterestReservationsBySitter(sitterId);
        return ResponseEntity.ok(list);
    }
}
