package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.PetOwnerProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetOwnerProfileRepository extends JpaRepository<PetOwnerProfileEntity, Integer> {
    Optional<PetOwnerProfileEntity> findByPetOwnerId(Integer petOwnerId);
}
