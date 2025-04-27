package com.start.pawpal_finder.service;

import com.start.pawpal_finder.Transformer;
import com.start.pawpal_finder.dto.InterestReservationDto;
import com.start.pawpal_finder.dto.NotificationMessageDto;
import com.start.pawpal_finder.dto.ReviewDto;
import com.start.pawpal_finder.entity.*;
import com.start.pawpal_finder.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.start.pawpal_finder.Transformer.toDto;
import static com.start.pawpal_finder.Transformer.toReviewDto;


@Service
public class InterestReservationService {

    private final InterestReservationRepository interestReservationRepository;
    private final PostRepository postRepository;
    private final PetSitterRepository petSitterRepository;
    private final WebSocketNotificationService webSocketNotificationService;
    private final PersistentNotificationService persistentNotificationService;
    private final ReviewRepository reviewRepository;
    @Autowired
    public InterestReservationService(InterestReservationRepository interestReservationRepository,
                                      PostRepository postRepository,
                                      PetOwnerRepository petOwnerRepository,
                                      PetSitterRepository petSitterRepository,
                                      WebSocketNotificationService webSocketNotificationService,
                                      PersistentNotificationService persistentNotificationService, ReviewRepository reviewRepository) {
        this.interestReservationRepository = interestReservationRepository;
        this.postRepository = postRepository;
        this.petSitterRepository = petSitterRepository;
        this.webSocketNotificationService = webSocketNotificationService;
        this.persistentNotificationService = persistentNotificationService;
        this.reviewRepository = reviewRepository;
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

        return toDto(savedInterest);
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

        if ("COMPLETED".equals(interest.getStatus())) {
            PostEntity post = interest.getPost();
            post.setStatus("COMPLETED");
            postRepository.save(post);
        }

        InterestReservationEntity updated = interestReservationRepository.save(interest);

        NotificationMessageDto notif = new NotificationMessageDto(
                "Interest Reservation Update",
                "Your interest reservation is now: " + updated.getStatus()
        );
        if ("ROLE_PET_OWNER".equalsIgnoreCase(role)) {
            webSocketNotificationService.sendNotificationToSitter(notif, interest.getPetSitter().getId());
            persistentNotificationService.saveNotification(
                    notif,
                    interest.getPetSitter().getId(),
                    interest.getPost().getId()
            );
        } else {
            webSocketNotificationService.sendNotificationToOwner(notif);
            persistentNotificationService.saveOwnerNotification(
                    notif,
                    interest.getPetOwner().getId(),
                    interest.getPost().getId()
            );
        }

        return toDto(updated);
    }


    @Transactional(readOnly=true)
    public InterestReservationDto getInterestByPostAndSitterId(Integer postId, Integer sitterId) {
        var entity = interestReservationRepository
                .findByPost_IdAndPetSitterId(postId, sitterId)
                .orElseThrow(() -> new RuntimeException("No interest for you on that post"));
        return toDto(entity);
    }

    @Transactional(readOnly=true)
    public InterestReservationDto getInterestByPostAndOwnerId(Integer postId, Integer ownerId) {
        var entity = interestReservationRepository
                .findByPost_IdAndPetOwner_Id(postId, ownerId)
                .orElseThrow(() -> new RuntimeException("No interest for you on that post"));
        return toDto(entity);
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

        return toDto(saved);
    }

    @Transactional(readOnly=true)
    public List<InterestReservationDto> getInterestReservationsBySitter(int sitterId) {
        List<InterestReservationEntity> ents =
                interestReservationRepository.findByPetSitter_Id(sitterId);
        return ents.stream().map(Transformer::toDto).toList();
    }

    public ReviewDto submitReviewOnInterest(int interestId, ReviewDto dto) {
        InterestReservationEntity ir = interestReservationRepository.findById(interestId)
                .orElseThrow(() -> new RuntimeException("No interest with ID " + interestId));
        if (!"COMPLETED".equals(ir.getStatus())) {
            throw new RuntimeException("Cannot review before completion");
        }

        ReviewEntity review = new ReviewEntity();
        review.setContent(dto.getContent());
        review.setRating(dto.getRating());
        review.setWrittenByRole(dto.getWrittenByRole());
        review.setWrittenById(dto.getWrittenById());
        review.setReviewedRole(dto.getReviewedRole());
        review.setReviewedId(dto.getReviewedId());
        review.setInterestReservation(ir);
        review.setCreatedAt(LocalDateTime.now());
        review.setWrittenByFirstName(ir.getPetOwner().getFirstName());
        review.setWrittenByLastName(ir.getPetOwner().getLastName());
        reviewRepository.save(review);

        return toReviewDto(review);
    }
}
