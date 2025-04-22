package com.start.pawpal_finder;

import com.start.pawpal_finder.dto.*;
import com.start.pawpal_finder.entity.*;
import com.start.pawpal_finder.representation.PricingModel;
import com.start.pawpal_finder.service.AnimalService;
import com.start.pawpal_finder.service.PetOwnerService;
import com.start.pawpal_finder.service.PetSitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        dto.setCity(entity.getCity());
        dto.setCounty(entity.getCounty());
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
        entity.setCity(dto.getCity());
        entity.setCounty(dto.getCounty());
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

    public static PostDto toDto(PostEntity post) {
        return new PostDto(
                post.getId(),
                post.getPetOwner().getId(),
                post.getTitle(),
                post.getDescription(),
                post.getStartDate(),
                post.getEndDate(),
                post.getStreet(),
                post.getNumber(),
                post.getTasks().stream().map(Transformer::toDto).toList(),
                post.getStatus(),
                post.getNotes(),
                post.getAnimals().stream().map(Transformer::toDto).collect(Collectors.toSet())
        );
    }

    public static PostEntity fromDto(PostDto dto, PetOwnerEntity petOwner, Set<AnimalEntity> animals) {
        List<TaskEntity> taskEntities = dto.getTasks() != null
                ? dto.getTasks().stream()
                .map(task -> new TaskEntity(null, task.getTask(), null))
                .collect(Collectors.toList())
                : List.of();

        PostEntity post = new PostEntity(
                dto.getId(),
                petOwner,
                dto.getTitle(),
                dto.getDescription(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getNumber(),
                dto.getStreet(),
                taskEntities,
                dto.getStatus(),
                dto.getNotes(),
                animals
        );

        taskEntities.forEach(task -> task.setPost(post));

        return post;
    }

    public static TaskDto toDto(TaskEntity taskEntity) {
        return new TaskDto(
                taskEntity.getId(),
                taskEntity.getTask()
        );
    }

    public static TaskEntity fromDto(TaskDto taskDto) {
        return new TaskEntity(
                taskDto.getId(),
                taskDto.getTask(),
                null
        );
    }

    public static PostSitterDto toDto(PostSitterEntity entity, List<PostSitterAvailabilityEntity> availability) {
        List<PostSitterAvailabilityDto> availabilityDtos =
                entity.getAvailabilities() == null ? Collections.emptyList() :
                        entity.getAvailabilities().stream()
                                .map(av -> new PostSitterAvailabilityDto(av.getDayOfWeek(), av.getStartTime(), av.getEndTime()))
                                .collect(Collectors.toList());

        return new PostSitterDto(
                entity.getId(),
                entity.getPetSitter().getId(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getPostDate(),
                entity.getTasks(),
                availabilityDtos,
                entity.getPricingModel().toString(),
                entity.getRatePerHour(),
                entity.getRatePerDay(),
                entity.getFlatRate()
        );
    }

    public static PostSitterEntity toEntity(PostSitterDto dto, PetSitterEntity petSitter) {
        PostSitterEntity entity = new PostSitterEntity();
        entity.setId(dto.getId());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        entity.setPostDate(dto.getPostDate());
        entity.setTasks(dto.getTasks());
        entity.setPricingModel(PricingModel.valueOf(dto.getPricingModel()));
        entity.setRatePerHour(dto.getRatePerHour());
        entity.setRatePerDay(dto.getRatePerDay());
        entity.setFlatRate(dto.getFlatRate());

        if (dto.getAvailability() != null && !dto.getAvailability().isEmpty()) {
            entity.setAvailabilityStart(dto.getAvailability().get(0).getStartTime());
            entity.setAvailabilityEnd(dto.getAvailability().get(dto.getAvailability().size() - 1).getEndTime());
        }
        entity.setPetSitter(petSitter);
        return entity;
    }


    public static PostSitterAvailabilityDto toDto(PostSitterAvailabilityEntity entity) {
        return new PostSitterAvailabilityDto(
                entity.getDayOfWeek(),
                entity.getStartTime(),
                entity.getEndTime()
        );
    }

    public static PostSitterAvailabilityEntity toEntity(PostSitterAvailabilityDto dto) {
        PostSitterAvailabilityEntity entity = new PostSitterAvailabilityEntity();
        entity.setDayOfWeek(dto.getDayOfWeek());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        return entity;
    }

    public static PetOwnerProfileDto toDto(PetOwnerProfileEntity profile) {
        return new PetOwnerProfileDto(
                profile.getId(),
                profile.getPetOwner().getId(),
                profile.getPetOwner().getFirstName(),
                profile.getPetOwner().getLastName(),
                profile.getPetOwner().getEmail(),
                profile.getPetOwner().getCity(),
                profile.getPetOwner().getCounty(),
                profile.getPetOwner().getPhoneNumber(),

                profile.getProfilePictureUrl() != null
                        ? "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(profile.getProfilePictureUrl())
                        : null,
                profile.getBudget(),
                profile.getBio(),
                profile.getNotificationsEnabled(),
                profile.getPreferredPaymentMethod()
        );
    }


    public static PetOwnerProfileEntity fromDto(PetOwnerProfileDto dto, PetOwnerEntity petOwner) {
        return new PetOwnerProfileEntity(
                dto.getId(),
                petOwner,
                dto.getProfilePictureUrl().getBytes(),
                dto.getBudget(),
                dto.getBio(),
                dto.getNotificationsEnabled(),
                dto.getPreferredPaymentMethod()
        );
    }

    public static ReviewDto toReviewDto(ReviewEntity review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setReservationId(review.getReservation().getId());
        dto.setContent(review.getContent());
        dto.setRating(review.getRating());
        dto.setCreatedAt(review.getCreatedAt().toString());
        dto.setWrittenByRole(review.getWrittenByRole());
        dto.setWrittenById(review.getWrittenById());
        dto.setReviewedRole(review.getReviewedRole());
        dto.setReviewedId(review.getReviewedId());
        dto.setWrittenByFirstName(review.getWrittenByFirstName());
        dto.setWrittenByLastName(review.getWrittenByLastName());
        return dto;
    }


    public static PetSitterProfileDto toDto(PetSitterProfileEntity entity) {
        PetSitterProfileDto dto = new PetSitterProfileDto();
        dto.setId(entity.getId());
        dto.setPetSitterId(entity.getPetSitter().getId());
        dto.setFirstName(entity.getPetSitter().getFirstName());
        dto.setLastName(entity.getPetSitter().getLastName());
        dto.setEmail(entity.getPetSitter().getEmail());
        dto.setCity(entity.getPetSitter().getCity());
        dto.setCounty(entity.getPetSitter().getCounty());
        dto.setPhoneNumber(entity.getPetSitter().getPhoneNumber());
        dto.setBirthDate(entity.getPetSitter().getBirthDate());
        dto.setBio(entity.getBio());
        dto.setNotificationsEnabled(entity.getNotificationsEnabled());
        dto.setPreferredPaymentMethod(entity.getPreferredPaymentMethod());
        dto.setExperience(entity.getExperience());
        dto.setStreet(entity.getStreet());
        dto.setStreetNumber(entity.getStreetNumber());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());

        if (entity.getProfilePictureUrl() != null) {
            dto.setProfilePictureUrl("data:image/jpeg;base64," +
                    Base64.getEncoder().encodeToString(entity.getProfilePictureUrl()));
        }

        return dto;
    }

    public static ReservationDto toDto(ReservationEntity entity) {
        assert entity.getPetOwner() != null;
        ReservationDto dto = new ReservationDto(
                entity.getId(),
                entity.getPostSitter() != null ? entity.getPostSitter().getId() : null,
                entity.getPetOwner().getId(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getFinalPrice(),
                null,
                null,
                null,
                null,
                entity.getPetOwner().getFirstName() + " " + entity.getPetOwner().getLastName()

        );

        if (entity.getPostSitter() != null) {
            dto.setPostDescription(entity.getPostSitter().getDescription());
            dto.setAvailabilityStart(String.valueOf(entity.getPostSitter().getAvailabilityStart()));
            dto.setAvailabilityEnd(String.valueOf(entity.getPostSitter().getAvailabilityEnd()));
            if (entity.getPostSitter().getPetSitter() != null) {
                String petSitterName = entity.getPostSitter().getPetSitter().getFirstName() + " "
                        + entity.getPostSitter().getPetSitter().getLastName();
                dto.setPetSitterName(petSitterName);
            }
        }

        return dto;
    }


    public static ReservationEntity toEntity(ReservationDto dto) {
        ReservationEntity entity = new ReservationEntity();
        entity.setId(dto.getId());

        if (dto.getPostSitterId() != null) {
            PostSitterEntity postSitter = new PostSitterEntity();
            postSitter.setId(dto.getPostSitterId());
            entity.setPostSitter(postSitter);
        }
        if (dto.getPetOwnerId() != null) {
            PetOwnerEntity petOwner = new PetOwnerEntity();
            petOwner.setId(dto.getPetOwnerId());
            entity.setPetOwner(petOwner);
        }
        entity.setStatus(dto.getStatus());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        entity.setFinalPrice(dto.getFinalPrice());
        return entity;
    }

    public static InterestReservationDto toDto(InterestReservationEntity interest) {
        if (interest == null) {
            return null;
        }
        InterestReservationDto dto = new InterestReservationDto();
        dto.setId(interest.getId());
        dto.setPostId(interest.getPost().getId());
        dto.setPetOwnerId(interest.getPetOwner().getId());
        dto.setPetSitterId(interest.getPetSitter().getId());
        dto.setStatus(interest.getStatus());
        dto.setMessage(interest.getMessage());
        dto.setCreatedAt(interest.getCreatedAt());
        dto.setUpdatedAt(interest.getUpdatedAt());
        dto.setSitterMarkedComplete(interest.isSitterMarkedComplete());
        dto.setOwnerMarkedComplete(interest.isOwnerMarkedComplete());
        return dto;
    }

    public static InterestReservationEntity fromDto(InterestReservationDto dto,
                                                    PostEntity post,
                                                    PetOwnerEntity petOwner,
                                                    PetSitterEntity petSitter) {
        if (dto == null) return null;

        InterestReservationEntity entity = new InterestReservationEntity();
        entity.setId(dto.getId());
        entity.setPost(post);
        entity.setPetOwner(petOwner);
        entity.setPetSitter(petSitter);
        entity.setStatus(dto.getStatus());
        entity.setMessage(dto.getMessage());
        entity.setSitterMarkedComplete(dto.isSitterMarkedComplete());
        entity.setOwnerMarkedComplete(dto.isOwnerMarkedComplete());
        return entity;
    }
}
