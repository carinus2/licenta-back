package com.start.pawpal_finder.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Type;
import org.hibernate.type.descriptor.jdbc.VarbinaryJdbcType;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "animal", uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
@ToString(exclude = "petOwner")
@Entity
public class AnimalEntity {

    @Id
    @SequenceGenerator(name = "animalGenerator", sequenceName = "sq_animal_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "animalGenerator")
    private Integer id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "street", nullable = false, length = 100)
    private String street;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "breed", nullable = false, length = 50)
    private String breed;

    @JdbcType(VarbinaryJdbcType.class)
    @Column(name = "profile_picture", columnDefinition = "BYTEA")
    private byte[] profilePicture;

    @ManyToOne
    @JoinColumn(name = "pet_owner_id", nullable = false)
    private PetOwnerEntity petOwner;

    @ManyToMany(mappedBy = "animals")
    private List<PetSitterEntity> petSitters;

}
