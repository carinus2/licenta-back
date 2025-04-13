package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.InterestReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterestReservationRepository extends JpaRepository<InterestReservationEntity, Integer> {
    InterestReservationEntity findInterestReservationByPostId(Integer postId);
    List<InterestReservationEntity> findByPetOwner_Id(Integer ownerId);

}
