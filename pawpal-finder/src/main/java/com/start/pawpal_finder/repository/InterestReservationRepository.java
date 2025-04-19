package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.InterestReservationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterestReservationRepository extends JpaRepository<InterestReservationEntity, Integer> {
    List<InterestReservationEntity> findAllByPost_Id(Integer postId);
    Page<InterestReservationEntity> findByPetOwner_Id(Integer ownerId, Pageable pageable);
    List<InterestReservationEntity> findByPetSitter_Id(Integer sitterId);
    Optional<InterestReservationEntity> findByPost_IdAndPetOwner_Id(Integer postId, Integer ownerId);
}
