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
    @Query("SELECT r FROM ReservationEntity r " +
            "JOIN r.postSitter ps " +
            "JOIN ps.petSitter sitter " +
            "WHERE sitter.id = :petSitterId")
    Page<ReservationEntity> findByPetSitterId(@Param("petSitterId") Integer petSitterId, Pageable pageable);

    @Query("SELECT r FROM ReservationEntity r " +
            "JOIN r.postSitter ps " +
            "JOIN ps.petSitter sitter " +
            "WHERE sitter.id = :petSitterId " +
            "AND r.status = :status")
    Page<ReservationEntity> findByPetSitterIdAndStatus(@Param("petSitterId") Integer petSitterId,
                                                       @Param("status") String status,
                                                       Pageable pageable);

    @Query("SELECT r FROM ReservationEntity r " +
            "JOIN r.postSitter ps " +
            "JOIN ps.petSitter sitter " +
            "JOIN r.petOwner po " +
            "WHERE sitter.id = :petSitterId " +
            "AND (LOWER(po.firstName) LIKE LOWER(CONCAT('%', :petOwnerName, '%')) " +
            "     OR LOWER(po.lastName) LIKE LOWER(CONCAT('%', :petOwnerName, '%')))")
    Page<ReservationEntity> findByPetSitterIdAndPetOwnerNameContainingIgnoreCase(
            @Param("petSitterId") Integer petSitterId,
            @Param("petOwnerName") String petOwnerName,
            Pageable pageable);

    @Query("SELECT r FROM ReservationEntity r " +
            "JOIN r.postSitter ps " +
            "JOIN ps.petSitter sitter " +
            "JOIN r.petOwner po " +
            "WHERE sitter.id = :petSitterId " +
            "AND r.status = :status " +
            "AND (LOWER(po.firstName) LIKE LOWER(CONCAT('%', :petOwnerName, '%')) " +
            "     OR LOWER(po.lastName) LIKE LOWER(CONCAT('%', :petOwnerName, '%')))")
    Page<ReservationEntity> findByPetSitterIdAndStatusAndPetOwnerNameContainingIgnoreCase(
            @Param("petSitterId") Integer petSitterId,
            @Param("status") String status,
            @Param("petOwnerName") String petOwnerName,
            Pageable pageable);



}
