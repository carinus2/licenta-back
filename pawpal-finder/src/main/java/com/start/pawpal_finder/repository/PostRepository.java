package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Integer> {
    List<PostEntity> findByStatus(String status);
    List<PostEntity> findByPetOwnerIdAndStatus(Integer petOwnerId, String status);
    Long countByPetOwnerIdAndStatus(Integer petOwnerId, String status);

}
