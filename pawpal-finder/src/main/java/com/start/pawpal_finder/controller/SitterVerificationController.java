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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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

    @GetMapping("/slots")
    public List<TimeSlotDto> getSlots(
            @RequestParam("date") @DateTimeFormat(iso = ISO.DATE) LocalDate date
    ) {
        return slotSvc.getAvailableOn(date).stream()
                .map(Transformer::fromEntity)
                .collect(Collectors.toList());
    }


    @PostMapping("/upload-id")
    public ResponseEntity<Void> uploadId(
            @RequestParam("file") MultipartFile file,
            @RequestParam("sitterId") Integer sitterId
    ) throws IOException {
        idService.save(file, sitterId);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/book-slot")
    public ResponseEntity<Void> bookSlot(
            @RequestBody SlotRequestRepresentation req
    ) {
        slotSvc.bookSlot(req.getSlotId(), req.getSitterId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/schedule-call")
    public ResponseEntity<Void> scheduleCall(
            @RequestBody ScheduleRequestRepresentation req
    ) {
        LocalDateTime dt = LocalDateTime.parse(req.getDateTime());
        slotSvc.bookCustomSlot(dt, req.getSitterId());
        return ResponseEntity.ok().build();
    }


    @PostMapping("/admin/verify-id/{id}")
    public ResponseEntity<Void> verifyId(@PathVariable Integer id) {
        idService.updateStatus(id.longValue(), UploadedIdStatus.VERIFIED);
        return ResponseEntity.ok().build();
    }

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
                .getBookedSlotsBySitterId(sitterId)
                .stream()
                .map(Transformer::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/admin/uploaded-ids")
    public ResponseEntity<Page<UploadedIdEntity>> getUploadedIds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UploadedIdEntity> paged = idService.getAllUploadedIds(pageable);
        return ResponseEntity.ok(paged);
    }

    @GetMapping("/admin/booked-slots")
    public ResponseEntity<Page<TimeSlotDto>> getBookedSlots(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TimeSlotDto> paged = slotSvc.getAllBookedSlots(pageable)
                .map(Transformer::fromEntity);
        return ResponseEntity.ok(paged);
    }

    @GetMapping(value = "/admin/id-image/{id}")
    public ResponseEntity<byte[]> getIdImage(@PathVariable Long id) {
        UploadedIdEntity entity = idService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        MediaType type = entity.getFileName().toLowerCase().endsWith(".png")
                ? MediaType.IMAGE_PNG
                : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok()
                .contentType(type)
                .body(entity.getFileData());
    }
}