package com.start.pawpal_finder.service;

import com.start.pawpal_finder.Transformer;
import com.start.pawpal_finder.dto.PetSitterDto;
import com.start.pawpal_finder.entity.PetSitterEntity;
import com.start.pawpal_finder.repository.PetSitterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PetSitterService {

    private final PetSitterRepository petSitterRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PetSitterService(PetSitterRepository petSitterRepository,
                            PasswordEncoder passwordEncoder) {
        this.petSitterRepository = petSitterRepository;
        this.passwordEncoder     = passwordEncoder;
    }

    /**
     * Registers a new pet sitter, encoding their password,
     * saving to the database, and returning a DTO with its generated ID.
     */
    public PetSitterDto registerPetSitter(PetSitterDto dto) {
        // 1) Map DTO → Entity
        PetSitterEntity entity = Transformer.fromDto(dto);

        // 2) Encode the raw password
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));

        // 3) Persist
        PetSitterEntity saved = petSitterRepository.save(entity);

        // 4) Map Entity → DTO (with ID) and return
        return Transformer.toDto(saved);
    }

    public Optional<PetSitterEntity> findByEmail(String email) {
        return petSitterRepository.findByEmail(email);
    }

    public boolean hasAnimals(String email) {
        return petSitterRepository.hasAnimals(email);
    }
}
