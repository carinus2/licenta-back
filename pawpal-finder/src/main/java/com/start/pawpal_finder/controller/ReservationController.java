package com.start.pawpal_finder.controller;

import com.start.pawpal_finder.dto.ReservationDto;
import com.start.pawpal_finder.service.ReservationService;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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

    @GetMapping("/owner/{petOwnerId}")
    public ResponseEntity<PagedModel<EntityModel<ReservationDto>>> getReservationsForPetOwner(
            @PathVariable Integer petOwnerId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "desc") String dateOrder,
            @RequestParam(required = false) String sitterName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            PagedResourcesAssembler<ReservationDto> assembler) {

        Page<ReservationDto> reservationsPage = reservationService.getReservationsForPetOwner(
                petOwnerId, status, dateOrder, sitterName, page, size);

        PagedModel<EntityModel<ReservationDto>> pagedModel = assembler.toModel(reservationsPage);

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("{petOwnerId}/count")
    public ResponseEntity<Long> getReservationCountForPetOwner(@PathVariable Integer petOwnerId) {
        long count = reservationService.getReservationCountForPetOwner(petOwnerId);
        return ResponseEntity.ok(count);
    }
}
