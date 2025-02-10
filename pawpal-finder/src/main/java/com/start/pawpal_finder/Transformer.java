package com.start.pawpal_finder;

import com.start.pawpal_finder.dto.AnimalDto;
import com.start.pawpal_finder.dto.PetOwnerDto;
import com.start.pawpal_finder.dto.PetSitterDto;
import com.start.pawpal_finder.entity.AnimalEntity;
import com.start.pawpal_finder.entity.PetOwnerEntity;
import com.start.pawpal_finder.entity.PetSitterEntity;
import com.start.pawpal_finder.service.AnimalService;
import com.start.pawpal_finder.service.PetOwnerService;
import com.start.pawpal_finder.service.PetSitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class Transformer {

    private final PetOwnerService petOwnerService;
    private final PetSitterService petSitterService;
    private final AnimalService animalService;

    @Autowired
    public Transformer(PetOwnerService petOwnerService, PetSitterService petSitterService, AnimalService animalService) {

        this.petOwnerService = petOwnerService;
        this.petSitterService = petSitterService;
        this.animalService = animalService;
    }

    public static PetOwnerDto toDto(PetOwnerEntity entity) {
        if (entity == null) {
            return null;
        }
        var dto = new PetOwnerDto();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setPassword(entity.getPassword());
        dto.setCity(entity.getCity());
        dto.setCounty(entity.getCounty());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setAdmin(entity.getAdmin());
        return dto;
    }

    public static PetOwnerEntity fromDto(PetOwnerDto dto) {
        if (dto == null) {
            return null;
        }
        var entity = new PetOwnerEntity();
        entity.setId(dto.getId());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setCity(dto.getCity());
        entity.setCounty(dto.getCounty());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setAdmin(dto.getAdmin());
        return entity;
    }

    public static PetSitterDto toDto(PetSitterEntity entity) {
        if (entity == null) {
            return null;
        }
        var dto = new PetSitterDto();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setPassword(entity.getPassword());
        dto.setAddress(entity.getAddress());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setBirthDate(entity.getBirthDate());
        dto.setAdmin(entity.getAdmin());
        return dto;
    }

    public static PetSitterEntity fromDto(PetSitterDto dto) {
        if (dto == null) {
            return null;
        }
        var entity = new PetSitterEntity();
        entity.setId(dto.getId());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setAddress(dto.getAddress());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setBirthDate(dto.getBirthDate());
        entity.setAdmin(dto.getAdmin());
        return entity;
    }

    public static AnimalDto toDto(AnimalEntity entity) {
        if (entity == null) {
            return null;
        }

        AnimalDto dto = new AnimalDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setStreet(entity.getStreet());
        dto.setDescription(entity.getDescription());
        dto.setAge(entity.getAge());
        dto.setBreed(entity.getBreed());

        if (entity.getProfilePicture() != null) {
            dto.setProfilePicture("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(entity.getProfilePicture()));
        } else {
            dto.setProfilePicture(null);
        }

        dto.setPetOwnerId(entity.getPetOwner() != null ? entity.getPetOwner().getId() : null);
        return dto;
    }

    public static AnimalEntity fromDto(AnimalDto dto, PetOwnerDto petOwner) {
        if (dto == null) {
            return null;
        }

        AnimalEntity entity = new AnimalEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setStreet(dto.getStreet());
        entity.setDescription(dto.getDescription());
        entity.setAge(dto.getAge());
        entity.setBreed(dto.getBreed());
        entity.setPetOwner(fromDto(petOwner));
        return entity;
    }

}
