package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.PetSitterProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetSitterProfileRepository extends JpaRepository<PetSitterProfileEntity, Integer> {
    Optional<PetSitterProfileEntity> findByPetSitterId(Integer petSitterId);
}
