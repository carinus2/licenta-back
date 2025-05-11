// com/start/pawpal_finder/entity/UploadedIdEntity.java
package com.start.pawpal_finder.entity;

import com.start.pawpal_finder.representation.UploadedIdStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.VarbinaryJdbcType;

import java.time.Instant;

@Entity
@Table(name = "uploaded_ids")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadedIdEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @JdbcType(VarbinaryJdbcType.class)
    @Column(name = "file_data", columnDefinition = "BYTEA")
    private byte[] fileData;

    private Instant uploadDate;

    @Enumerated(EnumType.STRING)
    private UploadedIdStatus status;
    private Integer sitterId;
}
