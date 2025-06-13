package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Integer> {
    List<PostEntity> findByStatus(String status);
    List<PostEntity> findByPetOwnerIdAndStatus(Integer petOwnerId, String status);
    Long countByPetOwnerIdAndStatus(Integer petOwnerId, String status);
    List<PostEntity> findByPetOwner_CityAndPetOwner_CountyAndStatus(String petOwner_city, String petOwner_county, String status);
    Page<PostEntity> findByPetOwnerIdAndStatus(
            Integer petOwnerId,
            String status,
            Pageable pageable
    );
}
