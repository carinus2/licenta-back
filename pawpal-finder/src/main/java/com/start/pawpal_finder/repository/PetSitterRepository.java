package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.PetSitterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetSitterRepository extends JpaRepository<PetSitterEntity, Integer> {
    PetSitterEntity findByEmail(String email);

}
