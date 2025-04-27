package com.start.pawpal_finder.service;

import com.start.pawpal_finder.Transformer;
import com.start.pawpal_finder.dto.LatLng;
import com.start.pawpal_finder.dto.PetSitterProfileDto;
import com.start.pawpal_finder.dto.ReviewDto;
import com.start.pawpal_finder.entity.PetSitterEntity;
import com.start.pawpal_finder.entity.PetSitterProfileEntity;
import com.start.pawpal_finder.entity.ReviewEntity;
import com.start.pawpal_finder.repository.PetSitterProfileRepository;
import com.start.pawpal_finder.repository.PetSitterRepository;
import com.start.pawpal_finder.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PetSitterProfileService {

    private final PetSitterProfileRepository profileRepository;
    private final PetSitterRepository petSitterRepository;
    private final ReviewRepository reviewRepository;
    private final GeocodingService geocodingService;
    @Autowired
    public PetSitterProfileService(PetSitterProfileRepository profileRepository,
                                   PetSitterRepository petSitterRepository, ReviewRepository reviewRepository, GeocodingService geocodingService) {
        this.profileRepository = profileRepository;
        this.petSitterRepository = petSitterRepository;
        this.reviewRepository = reviewRepository;
        this.geocodingService = geocodingService;
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
            dto.setStreet(profile.getStreet());
            dto.setStreetNumber(profile.getStreetNumber());
            dto.setLatitude(profile.getLatitude());
            dto.setLongitude(profile.getLongitude());

            if (profile.getProfilePictureUrl() != null) {
                dto.setProfilePictureUrl("data:image/jpeg;base64," +
                        Base64.getEncoder().encodeToString(profile.getProfilePictureUrl()));
            }
        }
        List<ReviewEntity> reviews = reviewRepository
                .findByReviewedRoleAndReviewedId("ROLE_PET_SITTER", sitterId);

        List<ReviewDto> reviewDtos = reviews.stream()
                .map(Transformer::toReviewDto)
                .collect(Collectors.toList());

        dto.setReviews(reviewDtos);
        return Optional.of(dto);
    }


    public PetSitterProfileDto saveProfile(
            PetSitterProfileDto dto,
            MultipartFile profilePicture
    ) {
        PetSitterEntity sitter = petSitterRepository.findById(dto.getPetSitterId())
                .orElseThrow(() -> new IllegalArgumentException("PetSitter not found"));

        // update sitter basics
        sitter.setCity(dto.getCity());
        sitter.setCounty(dto.getCounty());
        sitter.setPhoneNumber(dto.getPhoneNumber());
        petSitterRepository.save(sitter);

        // load or create profile entity
        PetSitterProfileEntity profile = profileRepository
                .findByPetSitterId(sitter.getId())
                .orElse(new PetSitterProfileEntity());
        profile.setPetSitter(sitter);
        profile.setBio(dto.getBio());
        profile.setNotificationsEnabled(dto.getNotificationsEnabled());
        profile.setPreferredPaymentMethod(dto.getPreferredPaymentMethod());
        profile.setExperience(dto.getExperience());
        profile.setStreet(dto.getStreet());
        profile.setStreetNumber(dto.getStreetNumber());

        LatLng resolved = resolveCoordinates(dto);
        profile.setLatitude(resolved.getLat());
        profile.setLongitude(resolved.getLng());

        // picture
        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                profile.setProfilePictureUrl(profilePicture.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Failed to save picture", e);
            }
        }

        // persist and return DTO
        PetSitterProfileEntity saved = profileRepository.save(profile);
        return Transformer.toDto(saved);
    }

    /**
     * Try in order:
     *  1) use client‐supplied coordinates,
     *  2) street‐level geocode,
     *  3) city‐level fallback.
     */
    private LatLng resolveCoordinates(PetSitterProfileDto dto) {
        // 1) client provided
        if (dto.getLatitude() != null && dto.getLongitude() != null) {
            return new LatLng(dto.getLatitude(), dto.getLongitude());
        }

        // build street‐level address
        String streetPart = Optional.ofNullable(dto.getStreet())
                .filter(s -> !s.isBlank())
                .map(s -> s + " " + dto.getStreetNumber() + ", ")
                .orElse("");

        String fullAddr = streetPart
                + dto.getCity() + ", "
                + dto.getCounty() + ", Romania";

        // 2) try street
        try {
            return geocodingService.geocode(fullAddr);
        } catch (IllegalStateException ise) {
            // 3) fallback to city only
            String cityOnly = dto.getCity() + ", " + dto.getCounty() + ", Romania";
            return geocodingService.geocode(cityOnly);
        }
    }


    public void deleteProfile(Integer sitterId) {
        profileRepository.findByPetSitterId(sitterId).ifPresent(profileRepository::delete);
    }

    public List<PetSitterProfileDto> findByCityAndCounty(String city, String county) {

        List<PetSitterProfileEntity> sitters = profileRepository.findPetSitterProfileEntitiesByPetSitter_CountyAndPetSitter_City(county, city);
        return sitters.stream().map(Transformer::toDto).toList();
    }

    public List<ReviewDto> getReviewsForSitter(Integer ownerId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ReviewEntity> reviewPage = reviewRepository.findByReviewedId(ownerId, pageRequest);
        return reviewPage.getContent().stream()
                .map(Transformer::toReviewDto)
                .collect(Collectors.toList());
    }
}
