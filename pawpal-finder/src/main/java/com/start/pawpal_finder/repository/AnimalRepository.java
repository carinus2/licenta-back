package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.AnimalEntity;
import com.start.pawpal_finder.entity.AnimalProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<AnimalEntity, Integer> {
    Long countByPetOwnerId(Integer petOwnerId);
    List<AnimalProjection> findByPetOwnerId(Integer id);
    @Query("SELECT COUNT(a) FROM AnimalEntity a WHERE a.petOwner.email = :email")
    int countByOwnerEmail(@Param("email") String email);

}
