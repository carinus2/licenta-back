package com.start.pawpal_finder.service;

import com.start.pawpal_finder.Transformer;
import com.start.pawpal_finder.dto.AnimalDto;
import com.start.pawpal_finder.entity.AnimalEntity;
import com.start.pawpal_finder.entity.AnimalProjection;
import com.start.pawpal_finder.entity.PetOwnerEntity;
import com.start.pawpal_finder.repository.AnimalRepository;
import com.start.pawpal_finder.repository.PetOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static com.start.pawpal_finder.auth.SecurityConfig.getAuthenticatedUserId;

@Service
public class AnimalService {

    @Autowired
    private AnimalRepository animalRepository;
    @Autowired
    private PetOwnerRepository petOwnerRepository;

    public Long countAnimalsByOwnerId(Integer ownerId) {
        return animalRepository.countByPetOwnerId(ownerId);
    }

    public AnimalDto saveAnimal(Integer ownerId, AnimalDto animalDto, MultipartFile profilePicture) {
        Integer authenticatedUserId = getAuthenticatedUserId();

        PetOwnerEntity petOwner = petOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Pet Owner with ID " + ownerId + " not found"));

        if (!petOwner.getId().equals(authenticatedUserId)) {
            throw new SecurityException("You are not authorized to modify this Pet Owner's animals");
        }

        AnimalEntity animalEntity = Transformer.fromDto(animalDto, Transformer.toDto(petOwner));

        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                byte[] bytes = profilePicture.getBytes();

                Path uploadDir = Paths.get("uploads");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                Path path = uploadDir.resolve(profilePicture.getOriginalFilename());
                Files.write(path, bytes);

                animalEntity.setProfilePicture(path.toString());
            } catch (IOException e) {
                throw new RuntimeException("Failed to save profile picture", e);
            }
        }

        AnimalEntity savedAnimal = animalRepository.save(animalEntity);
        return Transformer.toDto(savedAnimal);
    }

    public List<AnimalDto> getAnimalsByOwner(Integer petOwnerId) {
        List<AnimalProjection> entities = animalRepository.findByPetOwnerId(petOwnerId);

        return entities.stream()
                .map(entity -> new AnimalDto(
                        entity.getId(),
                        entity.getName(),
                        entity.getStreet(),
                        entity.getDescription(),
                        entity.getAge(),
                        entity.getBreed(),
                        entity.getProfilePicture(),
                        entity.getPetOwnerId()
                ))
                .collect(Collectors.toList());
    }

}

