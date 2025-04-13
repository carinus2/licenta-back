
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
@Table(name = "interest_reservation")
public class InterestReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @ManyToOne
    @JoinColumn(name = "pet_owner_id", nullable = false)
    private PetOwnerEntity petOwner;

    // The pet sitter who is interested in the post
    @ManyToOne
    @JoinColumn(name = "pet_sitter_id", nullable = false)
    private PetSitterEntity petSitter;

    // For example: "PENDING", "APPROVED", "REJECTED", "COMPLETED"
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "message", length = 500)
    private String message;

    // Timestamp fields
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "sitter_marked_complete", nullable = false)
    private boolean sitterMarkedComplete = false;

    @Column(name = "owner_marked_complete", nullable = false)
    private boolean ownerMarkedComplete = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
