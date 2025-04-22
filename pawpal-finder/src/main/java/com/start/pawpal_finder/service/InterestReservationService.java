package com.start.pawpal_finder.service;

import com.start.pawpal_finder.Transformer;
import com.start.pawpal_finder.dto.InterestReservationDto;
import com.start.pawpal_finder.dto.NotificationMessageDto;
import com.start.pawpal_finder.entity.InterestReservationEntity;
import com.start.pawpal_finder.entity.PetOwnerEntity;
import com.start.pawpal_finder.entity.PetSitterEntity;
import com.start.pawpal_finder.entity.PostEntity;
import com.start.pawpal_finder.repository.InterestReservationRepository;
import com.start.pawpal_finder.repository.PetOwnerRepository;
import com.start.pawpal_finder.repository.PetSitterRepository;
import com.start.pawpal_finder.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class InterestReservationService {

    private final InterestReservationRepository interestReservationRepository;
    private final PostRepository postRepository;
    private final PetOwnerRepository petOwnerRepository;
    private final PetSitterRepository petSitterRepository;
    private final WebSocketNotificationService webSocketNotificationService;
    private final PersistentNotificationService persistentNotificationService;

    @Autowired
    public InterestReservationService(InterestReservationRepository interestReservationRepository,
                                      PostRepository postRepository,
                                      PetOwnerRepository petOwnerRepository,
                                      PetSitterRepository petSitterRepository,
                                      WebSocketNotificationService webSocketNotificationService,
                                      PersistentNotificationService persistentNotificationService) {
        this.interestReservationRepository = interestReservationRepository;
        this.postRepository = postRepository;
        this.petOwnerRepository = petOwnerRepository;
        this.petSitterRepository = petSitterRepository;
        this.webSocketNotificationService = webSocketNotificationService;
        this.persistentNotificationService = persistentNotificationService;
    }

    @Transactional
    public InterestReservationDto createInterestReservation(Integer postId, Integer petSitterId, String message) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));
        PetOwnerEntity petOwner = post.getPetOwner();
        PetSitterEntity petSitter = petSitterRepository.findById(petSitterId)
                .orElseThrow(() -> new RuntimeException("Pet sitter not found with ID: " + petSitterId));

        InterestReservationEntity interest = new InterestReservationEntity();
        interest.setPost(post);
        interest.setPetOwner(petOwner);
        interest.setPetSitter(petSitter);
        interest.setStatus("PENDING");
        interest.setMessage(message);

        InterestReservationEntity savedInterest = interestReservationRepository.save(interest);

        NotificationMessageDto notif = new NotificationMessageDto(
                "New Interest in Your Post",
                petOwner.getFirstName() + " " + petSitter.getLastName() + " is interested in one of your posts."
        );
        Integer ownerId = petOwner.getId();
        webSocketNotificationService.sendNotificationToOwner(notif);
        persistentNotificationService.saveOwnerNotification(notif, ownerId, postId);

        return Transformer.toDto(savedInterest);
    }

    @Transactional
    public InterestReservationDto markInterestCompleted(Integer interestId, String role) {
        InterestReservationEntity interest = interestReservationRepository.findById(interestId)
                .orElseThrow(() -> new RuntimeException("Interest not found with ID: " + interestId));

        if ("ROLE_PET_OWNER".equalsIgnoreCase(role)) {
            interest.setOwnerMarkedComplete(true);
        } else if ("ROLE_PET_SITTER".equalsIgnoreCase(role)) {
            interest.setSitterMarkedComplete(true);
        } else {
            throw new RuntimeException("Unknown role: " + role);
        }

        if (interest.isOwnerMarkedComplete() && interest.isSitterMarkedComplete()) {
            interest.setStatus("COMPLETED");
        } else {
            interest.setStatus("AWAITING_COMPLETION_CONFIRMATION");
        }

        InterestReservationEntity updated = interestReservationRepository.save(interest);

        NotificationMessageDto notif = new NotificationMessageDto(
                "Interest Reservation Update",
                "Your interest reservation has been updated to: " + interest.getStatus() + "."
        );
        webSocketNotificationService.sendNotificationToOwner(notif);
        webSocketNotificationService.sendNotificationToSitter(notif, interest.getPetSitter().getId());
        persistentNotificationService.saveNotification(notif, interest.getPetSitter().getId(), interest.getPost().getId());
        persistentNotificationService.saveOwnerNotification(notif, interest.getPetOwner().getId(), interest.getPost().getId());

        return Transformer.toDto(updated);
    }

    @Transactional(readOnly=true)
    public InterestReservationDto getInterestByPostAndOwner(Integer postId, Integer sitterId) {
        var entity = interestReservationRepository
                .findByPost_IdAndPetOwner_Id(postId, sitterId)
                .orElseThrow(() -> new RuntimeException("No interest for you on that post"));
        return Transformer.toDto(entity);
    }

    @Transactional(readOnly = true)
    public Page<InterestReservationDto> getInterestReservationsForOwner(Integer ownerId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<InterestReservationEntity> pageResult = interestReservationRepository.findByPetOwner_Id(ownerId, pageable);
        return pageResult.map(Transformer::toDto);
    }

    @Transactional
    public InterestReservationDto updateStatus(
            Integer interestId,
            String newStatus,
            String ownerName
    ) {
        InterestReservationEntity interest = interestReservationRepository.findById(interestId)
                .orElseThrow(() -> new RuntimeException("Interest not found: " + interestId));

        interest.setStatus(newStatus);
        PostEntity post = interest.getPost();
        post.setStatus(newStatus.equals("APPROVED") ? "APPROVED" : post.getStatus());
        postRepository.save(post);

        InterestReservationEntity saved = interestReservationRepository.save(interest);

        NotificationMessageDto notif = new NotificationMessageDto(
                "Interest “" + newStatus + "”",
                "Your interest was " + newStatus.toLowerCase() +
                        " by " + ownerName + "."
        );
        webSocketNotificationService.sendNotificationToSitter(notif, interest.getPetSitter().getId());
        persistentNotificationService.saveNotification(
                notif,
                interest.getPetSitter().getId(),
                interest.getPost().getId()
        );

        return Transformer.toDto(saved);
    }

    @Transactional(readOnly=true)
    public List<InterestReservationDto> getInterestReservationsBySitter(int sitterId) {
        List<InterestReservationEntity> ents =
                interestReservationRepository.findByPetSitter_Id(sitterId);
        return ents.stream().map(Transformer::toDto).toList();
    }
}
