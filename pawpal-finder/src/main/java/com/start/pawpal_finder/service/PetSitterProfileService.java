package com.start.pawpal_finder.service;

import com.start.pawpal_finder.Transformer;
import com.start.pawpal_finder.dto.PetSitterProfileDto;
import com.start.pawpal_finder.entity.PetSitterEntity;
import com.start.pawpal_finder.entity.PetSitterProfileEntity;
import com.start.pawpal_finder.repository.PetSitterProfileRepository;
import com.start.pawpal_finder.repository.PetSitterRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PetSitterProfileService {

    private final PetSitterProfileRepository profileRepository;
    private final PetSitterRepository petSitterRepository;

    public PetSitterProfileService(PetSitterProfileRepository profileRepository,
                                   PetSitterRepository petSitterRepository) {
        this.profileRepository = profileRepository;
        this.petSitterRepository = petSitterRepository;
    }


    public Optional<PetSitterProfileDto> getProfileBySitterId(Integer sitterId) {
        Optional<PetSitterEntity> petSitterOpt = petSitterRepository.findById(sitterId);
        if (petSitterOpt.isEmpty()) {
            return Optional.empty();
        }

        PetSitterEntity petSitter = petSitterOpt.get();
        Optional<PetSitterProfileEntity> profileOpt = profileRepository.findByPetSitterId(sitterId);

        PetSitterProfileDto dto = new PetSitterProfileDto();
        dto.setPetSitterId(petSitter.getId());
        dto.setFirstName(petSitter.getFirstName());
        dto.setLastName(petSitter.getLastName());
        dto.setEmail(petSitter.getEmail());
        dto.setCity(petSitter.getCity());
        dto.setCounty(petSitter.getCounty());
        dto.setPhoneNumber(petSitter.getPhoneNumber());
        dto.setBirthDate(petSitter.getBirthDate());

        if (profileOpt.isPresent()) {
            PetSitterProfileEntity profile = profileOpt.get();
            dto.setId(profile.getId());
            dto.setBio(profile.getBio());
            dto.setNotificationsEnabled(profile.getNotificationsEnabled());
            dto.setPreferredPaymentMethod(profile.getPreferredPaymentMethod());
            dto.setExperience(profile.getExperience());

            if (profile.getProfilePictureUrl() != null) {
                dto.setProfilePictureUrl("data:image/jpeg;base64," +
                        Base64.getEncoder().encodeToString(profile.getProfilePictureUrl()));
            }

            if (profile.getReviews() != null) {
                dto.setReviews(
                        profile.getReviews().stream()
                                .map(Transformer::toDto)
                                .collect(Collectors.toList())
                );
            }
        }

        return Optional.of(dto);
    }


    public PetSitterProfileDto saveProfile(PetSitterProfileDto dto, MultipartFile profilePicture) {
        Optional<PetSitterEntity> petSitterOpt = petSitterRepository.findById(dto.getPetSitterId());
        if (petSitterOpt.isEmpty()) {
            throw new IllegalArgumentException("PetSitter not found");
        }

        PetSitterEntity petSitter = petSitterOpt.get();
        petSitter.setCity(dto.getCity());
        petSitter.setCounty(dto.getCounty());
        petSitter.setPhoneNumber(dto.getPhoneNumber());
        petSitterRepository.save(petSitter);

        PetSitterProfileEntity profile = profileRepository.findByPetSitterId(petSitter.getId())
                .orElse(new PetSitterProfileEntity());

        profile.setPetSitter(petSitter);
        profile.setBio(dto.getBio());
        profile.setNotificationsEnabled(dto.getNotificationsEnabled());
        profile.setPreferredPaymentMethod(dto.getPreferredPaymentMethod());
        profile.setExperience(dto.getExperience());

        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                profile.setProfilePictureUrl(profilePicture.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Failed to save profile picture", e);
            }
        }

        PetSitterProfileEntity savedProfile = profileRepository.save(profile);

        return Transformer.toDto(savedProfile);
    }


    public void deleteProfile(Integer sitterId) {
        profileRepository.findByPetSitterId(sitterId).ifPresent(profileRepository::delete);
    }
}
