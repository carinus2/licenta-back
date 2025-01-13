package com.start.pawpal_finder.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "review")
public class ReviewEntity {

    @Id
    @SequenceGenerator(name = "reviewGenerator", sequenceName = "sq_review_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reviewGenerator")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "animal_id", nullable = false)
    private AnimalEntity animal;

    @ManyToOne
    @JoinColumn(name = "pet_sitter_id", nullable = false)
    private PetSitterEntity petSitter;

    @Column(name = "rating", nullable = false)
    private Integer rating;

}
