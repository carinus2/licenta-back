package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Integer> {

    Page<ReviewEntity> findByReviewedId(Integer reviewedId, Pageable pageable);
}
