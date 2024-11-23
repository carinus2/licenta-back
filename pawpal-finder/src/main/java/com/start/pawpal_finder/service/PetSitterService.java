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

    @Autowired
    private PetSitterRepository petSitterRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerPetSitter(PetSitterDto petSitterDto) {
        // Convert DTO to Entity
        PetSitterEntity petSitter = Transformer.fromDto(petSitterDto);
        petSitter.setPassword(passwordEncoder.encode(petSitter.getPassword()));
        petSitterRepository.save(petSitter);
    }

    public Optional<PetSitterEntity> findByEmail(String email) {
        return petSitterRepository.findByEmail(email);
    }
}
