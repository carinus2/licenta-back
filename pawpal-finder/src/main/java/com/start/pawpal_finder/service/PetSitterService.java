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
    public PetSitterService(PetSitterRepository petSitterRepository, PasswordEncoder passwordEncoder) {
        this.petSitterRepository = petSitterRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean hasAnimals(String email) {
        return petSitterRepository.hasAnimals(email);
    }

    public void registerPetSitter(PetSitterDto petSitterDto) {
        PetSitterEntity petSitter = Transformer.fromDto(petSitterDto);
        petSitter.setPassword(passwordEncoder.encode(petSitterDto.getPassword()));
        petSitterRepository.save(petSitter);
    }

    public Optional<PetSitterEntity> findByEmail(String email) {
        return petSitterRepository.findByEmail(email);
    }
}
