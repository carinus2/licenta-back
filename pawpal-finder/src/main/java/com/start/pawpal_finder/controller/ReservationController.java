package com.start.pawpal_finder.controller;

import com.start.pawpal_finder.dto.ReservationDto;
import com.start.pawpal_finder.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationDto> createReservation(
            @RequestParam Integer postSitterId,
            @RequestParam Integer petOwnerId,
            @RequestParam(required = false) Double price
    ) {
        ReservationDto reservation = reservationService.createReservation(postSitterId, petOwnerId, price);
        return ResponseEntity.ok(reservation);
    }

    @PutMapping("/{reservationId}")
    public ResponseEntity<ReservationDto> updateReservationStatus(
            @PathVariable Integer reservationId,
            @RequestParam String status
    ) {
        ReservationDto updated = reservationService.updateReservationStatus(reservationId, status);
        return ResponseEntity.ok(updated);
    }
}
