package com.start.pawpal_finder.entity;

import com.start.pawpal_finder.representation.StatusModelTimeSlot;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Table(name = "time_slots")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TimeSlotEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long   id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer sitterId;
    @Enumerated(EnumType.STRING)
    private StatusModelTimeSlot status;
}

