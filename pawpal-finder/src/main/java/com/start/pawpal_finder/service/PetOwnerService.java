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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PetOwnerRepository petOwnerRepository;

    public Optional<PetOwnerEntity> findByEmail(String email) {
        return petOwnerRepository.findByEmail(email);
    }

    public void registerPetOwner(PetOwnerDto petOwnerDto) {
        PetOwnerEntity petOwner = Transformer.fromDto(petOwnerDto);
//        petOwner.setPassword(passwordEncoder.encode(petOwner.getPassword()));
        petOwnerRepository.save(petOwner);
    }
}
