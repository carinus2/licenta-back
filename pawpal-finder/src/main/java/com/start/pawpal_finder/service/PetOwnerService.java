package com.start.pawpal_finder.service;

import com.start.pawpal_finder.entity.PetOwnerEntity;
import com.start.pawpal_finder.repository.PetOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PetOwnerService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PetOwnerRepository petOwnerRepository;

//    public PetOwnerEntity registerPetOwner(PetOwnerEntity petOwner) {
//        petOwner.setAdmin(false);
//        return petOwnerRepository.save(petOwner);
//    }

    public PetOwnerEntity findByEmail(String email) {
        return petOwnerRepository.findByEmail(email);
    }

    public void registerPetOwner(PetOwnerEntity petOwner) {
        petOwner.setPassword(passwordEncoder.encode(petOwner.getPassword())); // Criptăm parola
        petOwnerRepository.save(petOwner);
    }
}
