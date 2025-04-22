package com.start.pawpal_finder.service;

import com.start.pawpal_finder.Transformer;
import com.start.pawpal_finder.dto.PetOwnerProfileDto;
import com.start.pawpal_finder.dto.ReviewDto;
import com.start.pawpal_finder.entity.PetOwnerEntity;
import com.start.pawpal_finder.entity.PetOwnerProfileEntity;
import com.start.pawpal_finder.entity.ReviewEntity;
import com.start.pawpal_finder.repository.PetOwnerProfileRepository;
import com.start.pawpal_finder.repository.PetOwnerRepository;
import com.start.pawpal_finder.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PetOwnerProfileService {

    private final PetOwnerProfileRepository profileRepository;
    private final PetOwnerRepository petOwnerRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public PetOwnerProfileService(PetOwnerProfileRepository profileRepository, PetOwnerRepository petOwnerRepository, ReviewRepository reviewRepository) {
        this.profileRepository = profileRepository;
        this.petOwnerRepository = petOwnerRepository;
        this.reviewRepository = reviewRepository;
    }

    public Optional<PetOwnerProfileDto> getProfileByOwnerId(Integer ownerId) {
        Optional<PetOwnerEntity> petOwnerOpt = petOwnerRepository.findById(ownerId);
        if (petOwnerOpt.isEmpty()) {
            return Optional.empty();
        }

        PetOwnerEntity petOwner = petOwnerOpt.get();
        Optional<PetOwnerProfileEntity> profileOpt = profileRepository.findByPetOwnerId(ownerId);

        PetOwnerProfileDto dto = new PetOwnerProfileDto();
        dto.setPetOwnerId(petOwner.getId());
        dto.setFirstName(petOwner.getFirstName());
        dto.setLastName(petOwner.getLastName());
        dto.setEmail(petOwner.getEmail());
        dto.setCity(petOwner.getCity());
        dto.setCounty(petOwner.getCounty());
        dto.setPhoneNumber(petOwner.getPhoneNumber());

        if (profileOpt.isPresent()) {
            PetOwnerProfileEntity profile = profileOpt.get();
            dto.setId(profile.getId());
            dto.setBio(profile.getBio());
            dto.setBudget(profile.getBudget());
            dto.setNotificationsEnabled(profile.getNotificationsEnabled());
            dto.setPreferredPaymentMethod(profile.getPreferredPaymentMethod());

            if (profile.getProfilePictureUrl() != null) {
                dto.setProfilePictureUrl("data:image/jpeg;base64," +
                        java.util.Base64.getEncoder().encodeToString(profile.getProfilePictureUrl()));
            }
        }

        return Optional.of(dto);
    }

    public PetOwnerProfileDto saveProfile(PetOwnerProfileDto dto, MultipartFile profilePicture) {
        Optional<PetOwnerEntity> petOwnerOpt = petOwnerRepository.findById(dto.getPetOwnerId());
        if (petOwnerOpt.isEmpty()) {
            throw new IllegalArgumentException("PetOwner not found");
        }

        PetOwnerEntity petOwner = petOwnerOpt.get();
        petOwner.setCity(dto.getCity());
        petOwner.setCounty(dto.getCounty());
        petOwner.setPhoneNumber(dto.getPhoneNumber());
        petOwnerRepository.save(petOwner);

        PetOwnerProfileEntity profile = profileRepository.findByPetOwnerId(petOwner.getId())
                .orElse(new PetOwnerProfileEntity());

        profile.setPetOwner(petOwner);
        profile.setBio(dto.getBio());
        profile.setBudget(dto.getBudget());
        profile.setNotificationsEnabled(dto.getNotificationsEnabled());
        profile.setPreferredPaymentMethod(dto.getPreferredPaymentMethod());

        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                profile.setProfilePictureUrl(profilePicture.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Failed to save profile picture", e);
            }
        }

        PetOwnerProfileEntity savedProfile = profileRepository.save(profile);

        return Transformer.toDto(savedProfile);
    }

    public void deleteProfile(Integer ownerId) {
        profileRepository.findByPetOwnerId(ownerId).ifPresent(profileRepository::delete);
    }

    public List<ReviewDto> getReviewsForOwner(Integer ownerId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ReviewEntity> reviewPage = reviewRepository.findByReviewedId(ownerId, pageRequest);
        return reviewPage.getContent().stream()
                .map(Transformer::toReviewDto)
                .collect(Collectors.toList());
    }
}
