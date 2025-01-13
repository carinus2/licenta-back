package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.PetSitterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetSitterRepository extends JpaRepository<PetSitterEntity, Integer> {
    @Query("SELECT p FROM PetSitterEntity p WHERE p.email = :email")
    Optional<PetSitterEntity> findByEmail(String email);

}
