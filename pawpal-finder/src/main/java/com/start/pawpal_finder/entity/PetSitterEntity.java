package com.start.pawpal_finder.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pet-sitter")
public class PetSitterEntity {

    @Id
    @SequenceGenerator(name = "petSitterGenerator", sequenceName = "sq_pet_sitter_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "petSitterGenerator")
    private Integer id;

    @Column(name = "first_name", length = 30, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 30, nullable = false)
    private String lastName;

    @Column(name = "email", length = 60, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 30, nullable = false)
    private String password;

    @Column(name = "address", length = 50, nullable = false)
    private String address;

    @Column(name = "phone_number", length = 11, nullable = false)
    private String phoneNumber;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "admin", nullable = false)
    private Boolean admin = false;

    @ManyToMany
    @JoinTable(
            name = "pet_sitter_animals",
            joinColumns = @JoinColumn(name = "pet_sitter_id"),
            inverseJoinColumns = @JoinColumn(name = "animal_id")
    )
    private List<AnimalEntity> animals;

}

