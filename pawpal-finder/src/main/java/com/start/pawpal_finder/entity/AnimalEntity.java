package com.start.pawpal_finder.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "animal")
public class AnimalEntity {

    @Id
    @SequenceGenerator(name = "animalGenerator", sequenceName = "sq_animal_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "animalGenerator")
    private Integer id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "street", nullable = false, length = 100)
    private String street;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "breed", nullable = false, length = 50)
    private String breed;

    @Column(name = "profile_picture", nullable = true, columnDefinition = "text")
    private String profilePicture;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL)
    private List<ReviewEntity> reviews;

    @ManyToOne
    @JoinColumn(name = "pet_owner_id", nullable = false)
    private PetOwnerEntity petOwner;

    @ManyToMany(mappedBy = "animals")
    private List<PetSitterEntity> petSitters;


}
