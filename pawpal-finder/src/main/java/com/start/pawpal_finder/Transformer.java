package com.start.pawpal_finder;

import com.start.pawpal_finder.dto.PetOwnerDto;
import com.start.pawpal_finder.dto.PetSitterDto;
import com.start.pawpal_finder.entity.PetOwnerEntity;
import com.start.pawpal_finder.entity.PetSitterEntity;
import com.start.pawpal_finder.service.PetOwnerService;
import com.start.pawpal_finder.service.PetSitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Transformer {

    private final PetOwnerService petOwnerService;
    private final PetSitterService petSitterService;

    @Autowired
    public Transformer(PetOwnerService petOwnerService, PetSitterService petSitterService){

        this.petOwnerService = petOwnerService;
        this.petSitterService = petSitterService;
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
        dto.setAddress(entity.getAddress());
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
        entity.setAddress(dto.getAddress());
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


}
