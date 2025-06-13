package com.start.pawpal_finder.service;

import com.start.pawpal_finder.Transformer;
import com.start.pawpal_finder.dto.LatLng;
import com.start.pawpal_finder.dto.PetOwnerProfileDto;
import com.start.pawpal_finder.dto.PetSitterProfileDto;
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
    private final GeocodingService geocodingService;

    public PetOwnerProfileService(PetOwnerProfileRepository profileRepository, PetOwnerRepository petOwnerRepository, ReviewRepository reviewRepository, GeocodingService geocodingService) {
        this.profileRepository = profileRepository;
        this.petOwnerRepository = petOwnerRepository;
        this.reviewRepository = reviewRepository;
        this.geocodingService = geocodingService;
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
            dto.setStreet(profile.getStreet());
            dto.setStreetNumber(profile.getStreetNumber());
            dto.setLatitude(profile.getLatitude());
            dto.setLongitude(profile.getLongitude());

            if (profile.getProfilePictureUrl() != null) {
                dto.setProfilePictureUrl("data:image/jpeg;base64," +
                        java.util.Base64.getEncoder().encodeToString(profile.getProfilePictureUrl()));
            }
        }

        return Optional.of(dto);
    }

    public List<PetOwnerProfileDto> getAllProfiles() {

        return profileRepository.findAll().stream().map(Transformer::toDto).collect(Collectors.toList());
    }


    public PetOwnerProfileDto saveProfile(
            PetOwnerProfileDto dto,
            MultipartFile profilePicture
    ) {
        PetOwnerEntity owner = petOwnerRepository.findById(dto.getPetOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("PetOwner not found"));

        // update basic owner info
        owner.setCity(dto.getCity());
        owner.setCounty(dto.getCounty());
        owner.setPhoneNumber(dto.getPhoneNumber());
        petOwnerRepository.save(owner);

        // load or new profile
        PetOwnerProfileEntity profile = profileRepository
                .findByPetOwnerId(owner.getId())
                .orElse(new PetOwnerProfileEntity());

        profile.setPetOwner(owner);
        profile.setBio(dto.getBio());
        profile.setBudget(dto.getBudget());
        profile.setNotificationsEnabled(dto.getNotificationsEnabled());
        profile.setPreferredPaymentMethod(dto.getPreferredPaymentMethod());

        // NEW: street & number from DTO
        profile.setStreet(dto.getStreet());
        profile.setStreetNumber(dto.getStreetNumber());

        // NEW: resolve lat/lng
        LatLng coords = resolveCoordinates(dto);
        profile.setLatitude(coords.getLat());
        profile.setLongitude(coords.getLng());

        // existing: picture handling
        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                profile.setProfilePictureUrl(profilePicture.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Failed to save profile picture", e);
            }
        }

        PetOwnerProfileEntity saved = profileRepository.save(profile);
        return Transformer.toDto(saved);
    }

    private LatLng resolveCoordinates(PetOwnerProfileDto dto) {
        // 1) client-provided
        if (dto.getLatitude() != null && dto.getLongitude() != null) {
            return new LatLng(dto.getLatitude(), dto.getLongitude());
        }

        // build street-level address if provided
        String streetPart = Optional.ofNullable(dto.getStreet())
                .filter(s -> !s.isBlank())
                .map(s -> s + " " + dto.getStreetNumber() + ", ")
                .orElse("");

        String fullAddr = streetPart
                + dto.getCity() + ", "
                + dto.getCounty() + ", Romania";

        try {
            // 2) street+city
            return geocodingService.geocode(fullAddr);
        } catch (IllegalStateException e) {
            // 3) city-only fallback
            return geocodingService.geocode(
                    dto.getCity() + ", " + dto.getCounty() + ", Romania"
            );
        }
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
