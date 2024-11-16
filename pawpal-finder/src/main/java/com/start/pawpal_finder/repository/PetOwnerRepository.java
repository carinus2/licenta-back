package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.PetOwnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetOwnerRepository extends JpaRepository<PetOwnerEntity, Integer> {
    PetOwnerEntity findByEmail(String email);

}
