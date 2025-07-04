package com.start.pawpal_finder.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.VarbinaryJdbcType;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pet_sitter_profile")
public class PetSitterProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "pet_sitter_id", nullable = false, unique = true)
    private PetSitterEntity petSitter;

    @JdbcType(VarbinaryJdbcType.class)
    @Column(name = "profile_picture_url", columnDefinition = "BYTEA")
    private byte[] profilePictureUrl;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "notifications_enabled")
    private Boolean notificationsEnabled = true;

    @Column(name = "preferred_payment_method", length = 20)
    private String preferredPaymentMethod = "Card";

    @Column(name = "experience")
    private Integer experience;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "street_number", nullable = false)
    private String streetNumber;

    @Column
    private Double latitude;

    @Column
    private Double longitude;
}
