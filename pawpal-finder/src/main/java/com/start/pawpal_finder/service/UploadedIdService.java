package com.start.pawpal_finder.service;

import com.start.pawpal_finder.entity.UploadedIdEntity;
import com.start.pawpal_finder.repository.UploadedIdRepository;
import com.start.pawpal_finder.representation.UploadedIdStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UploadedIdService {
    private final UploadedIdRepository repository;

    public UploadedIdService(UploadedIdRepository repository) {
        this.repository = repository;
    }


    public void save(MultipartFile file, Integer sitterId) throws IOException {
        // Generate unique filename
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // Save record to database
        UploadedIdEntity entity = new UploadedIdEntity();
        entity.setSitterId(sitterId);
        entity.setFileName(filename);
        entity.setStatus(UploadedIdStatus.PENDING);
        entity.setFileData(file.getBytes());

        repository.save(entity);
    }

    public List<UploadedIdEntity> findAll() {
        return repository.findAll();
    }

    public Optional<UploadedIdEntity> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<UploadedIdEntity> findBySitterId(Integer sitterId) {
        return repository.findBySitterId(sitterId);
    }

    /**
     * Returns all uploaded IDs for admin verification purposes.
     * Used by the admin dashboard to display all ID verifications.
     *
     * @return List of all uploaded ID entities
     */
    public List<UploadedIdEntity> getAllUploadedIds() {
        return repository.findAll();
    }

    public void updateStatus(Long id, UploadedIdStatus status) {
        Optional<UploadedIdEntity> entity = repository.findById(id);
        if (entity.isPresent()) {
            UploadedIdEntity updatedEntity = entity.get();
            updatedEntity.setStatus(status);
            repository.save(updatedEntity);
        } else {
            throw new RuntimeException("ID not found: " + id);
        }
    }
}