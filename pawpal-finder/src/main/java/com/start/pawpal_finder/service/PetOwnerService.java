package com.start.pawpal_finder.service;

import com.start.pawpal_finder.Transformer;
import com.start.pawpal_finder.dto.PetOwnerDto;
import com.start.pawpal_finder.entity.PetOwnerEntity;
import com.start.pawpal_finder.repository.PetOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PetOwnerService {

    private final PasswordEncoder passwordEncoder;
    private final PetOwnerRepository petOwnerRepository;

    @Autowired
    public PetOwnerService(PasswordEncoder passwordEncoder,
                           PetOwnerRepository petOwnerRepository) {
        this.passwordEncoder      = passwordEncoder;
        this.petOwnerRepository   = petOwnerRepository;
    }

    public Optional<PetOwnerEntity> findByEmail(String email) {
        return petOwnerRepository.findByEmail(email);
    }

    /**
     * Registers a new pet owner, encoding their password,
     * saving to the database, and returning a DTO with its generated ID.
     */
    public PetOwnerDto registerPetOwner(PetOwnerDto petOwnerDto) {
        // 1) Map DTO → Entity
        PetOwnerEntity entity = Transformer.fromDto(petOwnerDto);

        // 2) Encode the raw password
        entity.setPassword(passwordEncoder.encode(petOwnerDto.getPassword()));

        // 3) Persist
        PetOwnerEntity saved = petOwnerRepository.save(entity);

        // 4) Map Entity → DTO (now with ID populated) and return
        return Transformer.toDto(saved);
    }

    public boolean hasAnimals(String email) {
        return petOwnerRepository.hasAnimals(email);
    }
}
