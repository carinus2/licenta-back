package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.dto.FullNameProjection;
import com.start.pawpal_finder.entity.PetOwnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetOwnerRepository extends JpaRepository<PetOwnerEntity, Integer> {
    @Query("SELECT p FROM PetOwnerEntity p WHERE p.email = :email")
    Optional<PetOwnerEntity> findByEmail(@Param("email") String email);

    @Query("SELECT p.firstName, p.lastName FROM PetOwnerEntity p WHERE p.email = :email")
    Optional<FullNameProjection> findFullNameByEmail(@Param("email") String email);
}
