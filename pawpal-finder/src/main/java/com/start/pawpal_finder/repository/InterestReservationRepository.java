package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.InterestReservationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterestReservationRepository extends JpaRepository<InterestReservationEntity, Integer> {
    @Modifying
    @Transactional
    @Query("DELETE FROM InterestReservationEntity ir WHERE ir.post.id = :postId")
    void deleteByPostId(@Param("postId") Integer postId);
    List<InterestReservationEntity> findAllByPost_Id(Integer postId);
    Page<InterestReservationEntity> findByPetOwner_Id(Integer ownerId, Pageable pageable);
    List<InterestReservationEntity> findByPetSitter_Id(Integer sitterId);
    Optional<InterestReservationEntity> findByPost_IdAndPetOwner_Id(Integer postId, Integer ownerId);
    Optional<InterestReservationEntity> findByPost_IdAndPetSitterId(Integer postId, Integer ownerId);
    @Query("""
      SELECT i
        FROM InterestReservationEntity i
        JOIN i.petSitter ps
       WHERE i.petOwner.id       = :ownerId
         AND (:status    IS NULL OR i.status    = :status)
         AND (:sitterName IS NULL
              OR LOWER(ps.firstName) LIKE LOWER(CONCAT('%', :sitterName, '%'))
              OR LOWER(ps.lastName)  LIKE LOWER(CONCAT('%', :sitterName, '%'))
             )
    """)
    Page<InterestReservationEntity> findByPetOwnerWithFilters(
            @Param("ownerId")     Integer ownerId,
            @Param("status")      String status,
            @Param("sitterName")  String sitterName,
            Pageable pageable
    );

}
