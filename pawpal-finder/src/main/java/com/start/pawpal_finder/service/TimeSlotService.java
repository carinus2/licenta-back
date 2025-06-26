package com.start.pawpal_finder.service;

import com.start.pawpal_finder.entity.TimeSlotEntity;
import com.start.pawpal_finder.repository.TimeSlotRepository;
import com.start.pawpal_finder.representation.StatusModelTimeSlot;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class TimeSlotService {
    private final TimeSlotRepository repository;

    public TimeSlotService(TimeSlotRepository repository) {
        this.repository = repository;
    }

    public List<TimeSlotEntity> getAvailableOn(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return repository.findByStartTimeBetweenAndStatusNot(startOfDay, endOfDay, StatusModelTimeSlot.BOOKED);
    }

    public void cancelSlot(Long slotId) {
        TimeSlotEntity slot = repository.findById(slotId)
                .orElseThrow(() -> new EntityNotFoundException("Slot not found"));
        slot.setSitterId(null);      // or however you track who booked it
        slot.setStatus(StatusModelTimeSlot.AVAILABLE);
        repository.save(slot);
    }
    public void bookSlot(Long slotId, Integer sitterId) {
        TimeSlotEntity slot = repository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found: " + slotId));

        if (slot.getStatus().equals(StatusModelTimeSlot.BOOKED)) {
            throw new RuntimeException("Slot already booked");
        }

        slot.setStatus(StatusModelTimeSlot.BOOKED);
        slot.setSitterId(sitterId);
        repository.save(slot);
    }

    public void bookCustomSlot(LocalDateTime dateTime, Integer sitterId) {
        TimeSlotEntity slot = new TimeSlotEntity();
        slot.setStartTime(dateTime);
        slot.setEndTime(dateTime.plusMinutes(30)); // 30-minute slots
        slot.setStatus(StatusModelTimeSlot.BOOKED);
        slot.setSitterId(sitterId);
        repository.save(slot);
    }

    public List<TimeSlotEntity> getAllBookedSlots() {
        return repository.findByStatus(StatusModelTimeSlot.BOOKED);
    }

    public List<TimeSlotEntity> getBookedSlotsBySitterId(Integer sitterId) {
        return repository.findBySitterIdAndStatus(sitterId, StatusModelTimeSlot.BOOKED);
    }
    public boolean hasBookedSlot(Integer sitterId) {
        return repository.countBySitterId(sitterId) > 0;
    }

    public Page<TimeSlotEntity> getAllBookedSlots(Pageable pageable) {
        return repository.findByStatus(StatusModelTimeSlot.BOOKED, pageable);
    }
}