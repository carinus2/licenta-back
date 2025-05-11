package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.UploadedIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UploadedIdRepository extends JpaRepository<UploadedIdEntity, Long> {

    Optional<UploadedIdEntity> findBySitterId(Integer sitterId);

}

