package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.PetSitterProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PetSitterProfileRepository extends JpaRepository<PetSitterProfileEntity, Integer> {
    Optional<PetSitterProfileEntity> findByPetSitterId(Integer petSitterId);
    List<PetSitterProfileEntity> findPetSitterProfileEntitiesByPetSitter_CountyAndPetSitter_City(String county, String city);

    @Query(value = """
      SELECT *, (
        6371000 * 2 * ASIN(
          SQRT(
            POWER(SIN(RADIANS(p.latitude  - :lat) / 2), 2) +
            COS(RADIANS(:lat)) * COS(RADIANS(p.latitude)) *
            POWER(SIN(RADIANS(p.longitude - :lng) / 2), 2)
          )
        )
      ) AS distance_m
      FROM pet_sitter_profile p
      WHERE (
        6371000 * 2 * ASIN(
          SQRT(
            POWER(SIN(RADIANS(p.latitude  - :lat) / 2), 2) +
            COS(RADIANS(:lat)) * COS(RADIANS(p.latitude)) *
            POWER(SIN(RADIANS(p.longitude - :lng) / 2), 2)
          )
        )
      ) <= :radiusMeters
      ORDER BY distance_m
      """,
            nativeQuery = true)
    List<PetSitterProfileEntity> findAllByProximity(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusMeters") double radiusMeters
    );

}
