package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.PostSitterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostSitterRepository extends JpaRepository<PostSitterEntity, Integer> {

    List<PostSitterEntity> findByPetSitter_CityAndPetSitter_County(String city, String county);
    Page<PostSitterEntity> findByPetSitter_IdAndStatus(Integer sitterId, String status, Pageable pageable);
    long countByPetSitter_Id(Integer sitterId);
}
