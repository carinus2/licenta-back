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

    public void registerPetOwner(PetOwnerDto petOwnerDto) {

        PetOwnerEntity entity = Transformer.fromDto(petOwnerDto);
        entity.setPassword(passwordEncoder.encode(petOwnerDto.getPassword()));
        PetOwnerEntity saved = petOwnerRepository.save(entity);
        Transformer.toDto(saved);
    }

    public boolean hasAnimals(String email) {
        return petOwnerRepository.hasAnimals(email);
    }
}
