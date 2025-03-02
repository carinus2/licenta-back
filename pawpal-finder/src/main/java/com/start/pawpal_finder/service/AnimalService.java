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
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static com.start.pawpal_finder.auth.SecurityConfig.getAuthenticatedUserId;

@Service
public class AnimalService {


    private final AnimalRepository animalRepository;
    private final PetOwnerRepository petOwnerRepository;

    public AnimalService(AnimalRepository animalRepository, PetOwnerRepository petOwnerRepository) {
        this.animalRepository = animalRepository;
        this.petOwnerRepository = petOwnerRepository;
    }

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
                animalEntity.setProfilePicture(profilePicture.getBytes());
                System.out.println("Profile picture set in entity with size: " + profilePicture.getBytes().length);
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
                .map(entity -> {
                    String base64Image = entity.getProfilePicture() != null
                            ? "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(entity.getProfilePicture())
                            : null;

                    return new AnimalDto(
                            entity.getId(),
                            entity.getName(),
                            entity.getStreet(),
                            entity.getDescription(),
                            entity.getAge(),
                            entity.getBreed(),
                            base64Image,
                            entity.getPetOwnerId()
                    );
                })
                .collect(Collectors.toList());
    }

    public AnimalDto updateAnimal(Integer animalId, AnimalDto animalDto, MultipartFile profilePicture) {
        Integer authenticatedUserId = getAuthenticatedUserId();

        AnimalEntity existingAnimal = animalRepository.findById(animalId)
                .orElseThrow(() -> new IllegalArgumentException("Animal with ID " + animalId + " not found"));

        if (!existingAnimal.getPetOwner().getId().equals(authenticatedUserId)) {
            throw new SecurityException("You are not authorized to edit this animal");
        }

        existingAnimal.setName(animalDto.getName());
        existingAnimal.setStreet(animalDto.getStreet());
        existingAnimal.setDescription(animalDto.getDescription());
        existingAnimal.setAge(animalDto.getAge());
        existingAnimal.setBreed(animalDto.getBreed());

        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                existingAnimal.setProfilePicture(profilePicture.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Failed to update profile picture", e);
            }
        }

        AnimalEntity updatedAnimal = animalRepository.save(existingAnimal);
        return Transformer.toDto(updatedAnimal);
    }

    public void deleteAnimal(Integer animalId) {
        Integer authenticatedUserId = getAuthenticatedUserId();

        AnimalEntity animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new IllegalArgumentException("Animal with ID " + animalId + " not found"));

        if (!animal.getPetOwner().getId().equals(authenticatedUserId)) {
            throw new SecurityException("You are not authorized to delete this animal");
        }

        animalRepository.delete(animal);
    }

    public int countAnimalsByOwnerEmail(String email) {
        return animalRepository.countByOwnerEmail(email);
    }


}

