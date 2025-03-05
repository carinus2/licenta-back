package com.start.pawpal_finder.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pet-owner")
@ToString(exclude = "animals")
public class PetOwnerEntity {

    @Id
    @SequenceGenerator(name = "petOwnerGenerator", sequenceName = "sq_pet_owner_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "petOwnerGenerator")
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

    @Column(name = "phone_number", length = 11, nullable = false)
    private String phoneNumber;

    @Column(name = "admin", nullable = false)
    private Boolean admin = false;

    @OneToMany(mappedBy = "petOwner", cascade = CascadeType.ALL)
    private List<AnimalEntity> animals;

}

