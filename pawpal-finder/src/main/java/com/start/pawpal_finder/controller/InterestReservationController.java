package com.start.pawpal_finder.controller;

import com.start.pawpal_finder.dto.InterestReservationDto;
import com.start.pawpal_finder.service.InterestReservationService;
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

    @GetMapping("/get-by-post/{postId}")
    public ResponseEntity<InterestReservationDto> getInterestReservationByPostId(@PathVariable Integer postId) {
        InterestReservationDto dto = interestReservationService.getInterestReservationByPostId(postId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<InterestReservationDto>> getInterestReservationsForOwner(@PathVariable Integer ownerId) {
        List<InterestReservationDto> dtos = interestReservationService.getInterestReservationsForOwner(ownerId);
        return ResponseEntity.ok(dtos);
    }

}
