package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.AnimalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalRepository extends JpaRepository<AnimalEntity, Integer> {
    Long countByPetOwnerId(Integer petOwnerId);
}
