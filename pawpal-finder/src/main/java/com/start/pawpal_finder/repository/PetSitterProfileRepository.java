package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.PetSitterProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PetSitterProfileRepository extends JpaRepository<PetSitterProfileEntity, Integer> {
    Optional<PetSitterProfileEntity> findByPetSitterId(Integer petSitterId);

    List<PetSitterProfileEntity> findPetSitterProfileEntitiesByPetSitter_CountyAndPetSitter_City(String county, String city);
}
