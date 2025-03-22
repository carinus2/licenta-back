package com.start.pawpal_finder.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "post_sitter")
public class PostSitterEntity {

    @Id
    @SequenceGenerator(name = "postSitterGenerator", sequenceName = "sq_post_sitter_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "postSitterGenerator")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "pet_sitter_id", nullable = false)
    private PetSitterEntity petSitter;

    @Column(name = "availability_start", nullable = false)
    private LocalTime availabilityStart;

    @Column(name = "availability_end", nullable = false)
    private LocalTime availabilityEnd;

    @Column(name = "post_date", nullable = false)
    private LocalDate postDate;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "description", length = 500)
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_sitter_tasks", joinColumns = @JoinColumn(name = "post_sitter_id"))
    @Column(name = "task_name")
    private List<String> tasks;

    @OneToMany(mappedBy = "postSitter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostSitterAvailabilityEntity> availabilities;
}
