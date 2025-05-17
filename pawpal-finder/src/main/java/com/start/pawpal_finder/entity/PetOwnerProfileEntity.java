package com.start.pawpal_finder.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.VarbinaryJdbcType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pet_owner_profile")
public class PetOwnerProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "pet_owner_id", nullable = false, unique = true)
    private PetOwnerEntity petOwner;

    @JdbcType(VarbinaryJdbcType.class)
    @Column(name = "profile_picture_url", columnDefinition = "BYTEA")
    private byte[] profilePictureUrl;

    @Column(name = "budget", nullable = false, precision = 10, scale = 2)
    private BigDecimal budget;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "notifications_enabled")
    private Boolean notificationsEnabled = true;

    @Column(name = "preferred_payment_method", length = 20)
    private String preferredPaymentMethod = "Card";

    @Column(name = "street")
    private String street;

    @Column(name = "street_number")
    private String streetNumber;

    @Column
    private Double latitude;

    @Column
    private Double longitude;
}
