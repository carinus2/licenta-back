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

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "city", length = 50, nullable = false)
    private String city;

    @Column(name = "county", length = 50, nullable = false)
    private String county;

    @Column(name = "phone_number", length = 13, nullable = false)
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

    @OneToOne(mappedBy = "petSitter", cascade = CascadeType.ALL, optional = true, fetch = FetchType.LAZY)
    private PetSitterProfileEntity profile;

    public int getExperience() {
        return (profile == null) ? 0 : profile.getExperience();
    }
}

