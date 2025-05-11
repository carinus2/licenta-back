package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.TimeSlotEntity;
import com.start.pawpal_finder.representation.StatusModelTimeSlot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

public interface TimeSlotRepository extends JpaRepository<TimeSlotEntity, Long> {
    List<TimeSlotEntity> findByStartTimeBetweenAndStatusNot(
            LocalDateTime startTime,
            LocalDateTime endTime,
            StatusModelTimeSlot excludeStatus);

    List<TimeSlotEntity> findByStatus(StatusModelTimeSlot status);

    Integer countBySitterId(Integer sitterId);
    List<TimeSlotEntity> findBySitterIdAndStatus(
            Integer sitterId,
            StatusModelTimeSlot status
    );

    Page<TimeSlotEntity> findByStatus(StatusModelTimeSlot status, Pageable pageable);

    boolean existsByStartTimeAndEndTime(LocalDateTime startTime, LocalDateTime endTime);
}
