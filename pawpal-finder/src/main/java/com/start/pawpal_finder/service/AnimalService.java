package com.start.pawpal_finder.service;

import com.start.pawpal_finder.Transformer;
import com.start.pawpal_finder.dto.AnimalDto;
import com.start.pawpal_finder.entity.AnimalEntity;
import com.start.pawpal_finder.entity.PetOwnerEntity;
import com.start.pawpal_finder.repository.AnimalRepository;
import com.start.pawpal_finder.repository.PetOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.start.pawpal_finder.auth.SecurityConfig.getAuthenticatedUsername;

@Service
public class AnimalService {

    @Autowired
    private AnimalRepository animalRepository;
    @Autowired
    private PetOwnerRepository petOwnerRepository;

    public Long countAnimalsByOwnerId(Integer ownerId) {
        return animalRepository.countByPetOwnerId(ownerId);
    }

    public AnimalDto saveAnimal(Integer ownerId, AnimalDto animalDto) {
        String authenticatedUsername = getAuthenticatedUsername();

        PetOwnerEntity petOwner = petOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Pet Owner with ID " + ownerId + " not found"));

        if (!petOwner.getEmail().equals(authenticatedUsername)) {
            throw new SecurityException("You are not authorized to modify this Pet Owner's animals");
        }

        AnimalEntity animalEntity = Transformer.fromDto(animalDto, Transformer.toDto(petOwner));

        AnimalEntity savedAnimal = animalRepository.save(animalEntity);

        return Transformer.toDto(savedAnimal);
    }

}

