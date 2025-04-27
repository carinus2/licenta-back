package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.PostSitterAvailabilityEntity;
import com.start.pawpal_finder.entity.PostSitterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PostSitterAvailabilityRepository extends JpaRepository<PostSitterAvailabilityEntity, Integer> {

    List<PostSitterAvailabilityEntity> findByPostSitter(PostSitterEntity postSitter);
    List<PostSitterAvailabilityEntity> findByPostSitterAndPostSitter_Status(PostSitterEntity postSitter, String status);
    @Transactional
    void deleteByPostSitter(PostSitterEntity postSitter);
}
