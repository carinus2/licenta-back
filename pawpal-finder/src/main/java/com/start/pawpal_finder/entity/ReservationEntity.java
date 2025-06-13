package com.start.pawpal_finder.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservation")
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "post_sitter_id", nullable = false)
    private PostSitterEntity postSitter;

    @ManyToOne
    @JoinColumn(name = "pet_owner_id", nullable = false)
    private PetOwnerEntity petOwner;

    @Column(name = "status", length = 50, nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "final_price")
    private Double finalPrice;

    @Column(name = "sitter_marked_complete", nullable = false)
    private boolean sitterMarkedComplete = false;

    @Column(name = "owner_marked_complete", nullable = false)
    private boolean ownerMarkedComplete = false;
}

