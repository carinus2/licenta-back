package com.start.pawpal_finder.service;

import com.start.pawpal_finder.entity.TimeSlotEntity;
import com.start.pawpal_finder.repository.TimeSlotRepository;
import com.start.pawpal_finder.representation.StatusModelTimeSlot;
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

    public List<TimeSlotEntity> getBookedSlots() {
        // Updated to use status instead of booked flag
        return repository.findByStatus(StatusModelTimeSlot.BOOKED);
    }

    /**
     * Returns all booked time slots for admin verification purposes.
     * Used by the admin dashboard to display all scheduled verification calls.
     *
     * @return List of all booked time slots
     */
    public List<TimeSlotEntity> getAllBookedSlots() {
        return repository.findByStatus(StatusModelTimeSlot.BOOKED);
    }

    public List<TimeSlotEntity> getBookedSlotsBySitterId(Integer sitterId) {
        return repository.findBySitterIdAndStatus(sitterId, StatusModelTimeSlot.BOOKED);
    }
    public boolean hasBookedSlot(Integer sitterId) {
        return repository.countBySitterId(sitterId) > 0;
    }
}