package com.start.pawpal_finder.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "review")
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer rating;      // rating 1..5
    private String content;      // conținutul review-ului

    @ManyToOne
    @JoinColumn(name = "pet_sitter_profile_id")
    private PetSitterProfileEntity petSitterProfile;
}
