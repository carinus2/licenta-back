package com.start.pawpal_finder.service;

import com.start.pawpal_finder.entity.PetSitterEntity;
import com.start.pawpal_finder.repository.PetSitterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PetSitterService {

    @Autowired
    private PetSitterRepository petSitterRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
//    public PetSitterEntity registerPetSitter(PetSitterEntity petSitter) {
//        petSitter.setAdmin(false);
//        return petSitterRepository.save(petSitter);
//    }

    public void registerPetSitter(PetSitterEntity petSitter) {
        petSitter.setPassword(passwordEncoder.encode(petSitter.getPassword())); // Criptăm parola
        petSitterRepository.save(petSitter);
    }

    public PetSitterEntity findByEmail(String email) {
        return petSitterRepository.findByEmail(email);
    }
}
