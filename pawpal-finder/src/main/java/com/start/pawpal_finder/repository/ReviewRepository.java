package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Integer> {
}
