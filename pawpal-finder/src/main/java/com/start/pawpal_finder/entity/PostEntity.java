package com.start.pawpal_finder.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "post")
public class PostEntity {

    @Id
    @SequenceGenerator(name = "postGenerator", sequenceName = "sq_post_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "postGenerator")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "pet_owner_id", nullable = false)
    private PetOwnerEntity petOwner;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "description", length = 1000, nullable = false)
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "number", length = 50, nullable = false)
    private Integer number;

    @Column(name = "street", length = 50, nullable = false)
    private String street;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<TaskEntity> tasks;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "notes", length = 200)
    private String notes;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "post_animals",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "animal_id")
    )
    private Set<AnimalEntity> animals;

}
