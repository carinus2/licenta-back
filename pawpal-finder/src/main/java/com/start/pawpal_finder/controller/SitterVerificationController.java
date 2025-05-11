package com.start.pawpal_finder.controller;

import com.start.pawpal_finder.Transformer;
import com.start.pawpal_finder.dto.SitterVerificationStatusDto;
import com.start.pawpal_finder.dto.TimeSlotDto;
import com.start.pawpal_finder.entity.UploadedIdEntity;
import com.start.pawpal_finder.representation.SlotRequestRepresentation;
import com.start.pawpal_finder.representation.ScheduleRequestRepresentation;
import com.start.pawpal_finder.representation.UploadedIdStatus;
import com.start.pawpal_finder.service.TimeSlotService;
import com.start.pawpal_finder.service.UploadedIdService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sitter/verification")
public class SitterVerificationController {

    private final TimeSlotService slotSvc;
    private final UploadedIdService idService;

    public SitterVerificationController(TimeSlotService slotSvc, UploadedIdService idService) {
        this.slotSvc = slotSvc;
        this.idService = idService;
    }

    /**
     * Returns whether the sitter's ID is verified, whether they have any booked slot,
     * and whether both are true (fully verified).
     */
    @GetMapping("/status/{sitterId}")
    public ResponseEntity<SitterVerificationStatusDto> getVerificationStatus(
            @PathVariable Integer sitterId
    ) {
        Optional<UploadedIdEntity> uploadedId = idService.findBySitterId(sitterId);
        boolean idVerified = uploadedId.isPresent()
                && uploadedId.get().getStatus() == UploadedIdStatus.VERIFIED;
        boolean callScheduled = slotSvc.hasBookedSlot(sitterId);
        boolean fullyVerified = idVerified && callScheduled;

        SitterVerificationStatusDto dto =
                new SitterVerificationStatusDto(idVerified, callScheduled, fullyVerified);
        return ResponseEntity.ok(dto);
    }

    /**
     * Returns all available 30-minute slots on the given date.
     * Angular will call: GET /api/sitter/verification/slots?date=YYYY-MM-DD
     */
    @GetMapping("/slots")
    public List<TimeSlotDto> getSlots(
            @RequestParam("date") @DateTimeFormat(iso = ISO.DATE) LocalDate date
    ) {
        return slotSvc.getAvailableOn(date).stream()
                .map(Transformer::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Handles ID-photo uploads. Expects a multipart/form-data POST with
     * "file"=<the JPEG/PNG blob> and "sitterId"=<the sitter's user ID>.
     */
    @PostMapping("/upload-id")
    public ResponseEntity<Void> uploadId(
            @RequestParam("file") MultipartFile file,
            @RequestParam("sitterId") Integer sitterId
    ) throws IOException {
        idService.save(file, sitterId);
        return ResponseEntity.ok().build();
    }


    /**
     * Books one of the pre-defined slots.
     * Angular will send { slotId: 123, sitterId: 456 } in the JSON body.
     */
    @PostMapping("/book-slot")
    public ResponseEntity<Void> bookSlot(
            @RequestBody SlotRequestRepresentation req
    ) {
        slotSvc.bookSlot(req.getSlotId(), req.getSitterId());
        return ResponseEntity.ok().build();
    }

    /**
     * Schedules a custom‐time call when no predefined slots work.
     * Angular will send { dateTime: "2025-05-11T14:30", sitterId: 456 }.
     */
    @PostMapping("/schedule-call")
    public ResponseEntity<Void> scheduleCall(
            @RequestBody ScheduleRequestRepresentation req
    ) {
        // Parse the incoming ISO-8601 date/time string
        LocalDateTime dt = LocalDateTime.parse(req.getDateTime());
        slotSvc.bookCustomSlot(dt, req.getSitterId());
        return ResponseEntity.ok().build();
    }

    /*
     * Admin-specific endpoints that match exactly what the frontend is calling
     */

    /**
     * Returns all booked time slots for admin verification panel.
     * Matches exactly the URL used by the Angular admin dashboard component.
     */
    @GetMapping("/admin/booked-slots")
    public ResponseEntity<List<TimeSlotDto>> getBookedSlots() {
        return ResponseEntity.ok(
                slotSvc.getAllBookedSlots().stream()
                        .map(Transformer::fromEntity)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Returns all uploaded IDs for admin verification panel.
     * Matches exactly the URL used by the Angular admin dashboard component.
     */
    @GetMapping("/admin/uploaded-ids")
    public ResponseEntity<List<UploadedIdEntity>> getUploadedIds() {
        return ResponseEntity.ok(idService.getAllUploadedIds());
    }

    /**
     * Verifies (approves) a sitter's uploaded ID.
     * Matches exactly the URL used by the Angular admin dashboard component.
     */
    @PostMapping("/admin/verify-id/{id}")
    public ResponseEntity<Void> verifyId(@PathVariable Integer id) {
        idService.updateStatus(id.longValue(), UploadedIdStatus.VERIFIED);
        return ResponseEntity.ok().build();
    }

    /**
     * Rejects a sitter's uploaded ID.
     * Matches exactly the URL used by the Angular admin dashboard component.
     */
    @PostMapping("/admin/reject-id/{id}")
    public ResponseEntity<Void> rejectId(@PathVariable Integer id) {
        idService.updateStatus(id.longValue(), UploadedIdStatus.REJECTED);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/booked-slot/{sitterId}")
    public ResponseEntity<List<TimeSlotDto>> getBookedSlot(
            @PathVariable Integer sitterId
    ) {
        List<TimeSlotDto> dtos = slotSvc
                // you’ll need a service method that finds the booked slot(s) for this sitter)
                .getBookedSlotsBySitterId(sitterId)
                .stream()
                .map(Transformer::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}