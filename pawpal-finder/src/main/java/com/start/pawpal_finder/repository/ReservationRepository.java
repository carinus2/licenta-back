package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.ReservationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ReservationRepository extends PagingAndSortingRepository<ReservationEntity, Integer> , JpaRepository<ReservationEntity, Integer> {

    Page<ReservationEntity> findByPetOwnerId(Integer petOwnerId, Pageable pageable);

    Page<ReservationEntity> findByPetOwnerIdAndStatus(Integer petOwnerId, String status, Pageable pageable);

    @Query("SELECT r FROM ReservationEntity r " +
            "JOIN r.postSitter ps " +
            "JOIN ps.petSitter p " +
            "WHERE r.petOwner.id = :petOwnerId " +
            "AND (LOWER(p.firstName) LIKE LOWER(CONCAT('%', :sitterName, '%')) " +
            "     OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :sitterName, '%')))")
    Page<ReservationEntity> findByPetOwnerIdAndSitterNameContainingIgnoreCase(@Param("petOwnerId") Integer petOwnerId,
                                                                              @Param("sitterName") String sitterName,
                                                                              Pageable pageable);

    @Query("SELECT r FROM ReservationEntity r " +
            "JOIN r.postSitter ps " +
            "JOIN ps.petSitter p " +
            "WHERE r.petOwner.id = :petOwnerId " +
            "AND r.status = :status " +
            "AND (LOWER(p.firstName) LIKE LOWER(CONCAT('%', :sitterName, '%')) " +
            "     OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :sitterName, '%')))")
    Page<ReservationEntity> findByPetOwnerIdAndStatusAndSitterNameContainingIgnoreCase(@Param("petOwnerId") Integer petOwnerId,
                                                                                       @Param("status") String status,
                                                                                       @Param("sitterName") String sitterName,
                                                                                       Pageable pageable);

    long countByPetOwnerId(Integer petOwnerId);

}
