package com.start.pawpal_finder.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "review")
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationEntity reservation;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private int rating;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "written_by_role", nullable = false)
    private String writtenByRole;

    @Column(name = "written_by_id", nullable = false)
    private Integer writtenById;

    @Column(name = "reviewed_role", nullable = false)
    private String reviewedRole;

    @Column(name = "reviewed_id", nullable = false)
    private Integer reviewedId;

    @Column(name = "written_by_first_name", nullable = false)
    private String writtenByFirstName;

    @Column(name = "written_by_last_name", nullable = false)
    private String writtenByLastName;

}
