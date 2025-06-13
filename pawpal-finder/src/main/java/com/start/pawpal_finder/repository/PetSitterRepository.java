package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.dto.PetSitterDto;
import com.start.pawpal_finder.entity.PetSitterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetSitterRepository extends JpaRepository<PetSitterEntity, Integer> {
    Optional<PetSitterEntity> findByEmail(String email);

    @Query("SELECT COUNT(a) > 0 " +
            "FROM PetSitterEntity ps " +
            "JOIN ps.animals a " +
            "WHERE ps.email = :email")
    boolean hasAnimals(@Param("email") String email);

}
