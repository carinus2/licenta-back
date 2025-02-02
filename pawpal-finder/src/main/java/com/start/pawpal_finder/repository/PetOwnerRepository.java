package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.PetOwnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetOwnerRepository extends JpaRepository<PetOwnerEntity, Integer> {
    Optional<PetOwnerEntity> findByEmail(@Param("email") String email);

    @Query("SELECT COUNT(a) > 0 " +
            "FROM AnimalEntity a " +
            "WHERE a.petOwner.email = :email")
    boolean hasAnimals(@Param("email") String email);

}
