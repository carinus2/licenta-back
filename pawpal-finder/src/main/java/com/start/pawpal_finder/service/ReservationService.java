package com.start.pawpal_finder.service;

import com.start.pawpal_finder.Transformer;
import com.start.pawpal_finder.dto.NotificationMessageDto;
import com.start.pawpal_finder.dto.ReservationDto;
import com.start.pawpal_finder.dto.ReviewDto;
import com.start.pawpal_finder.entity.*;
import com.start.pawpal_finder.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final PostSitterRepository postSitterRepository;
    private final PostSitterService postSitterService;
    private final PetOwnerRepository petOwnerRepository;
    private final WebSocketNotificationService webSocketNotificationService;
    private final PersistentNotificationService persistentNotificationService;
    private final ReviewRepository reviewRepository;
    @Autowired
    public ReservationService(ReservationRepository reservationRepository,
                              PostSitterRepository postSitterRepository,
                              PetOwnerRepository petOwnerRepository,
                              WebSocketNotificationService webSocketNotificationService,
                              PersistentNotificationService persistentNotificationService, ReviewRepository reviewRepository, PetSitterProfileRepository petSitterProfileRepository, PostSitterService postSitterService) {
        this.reservationRepository = reservationRepository;
        this.postSitterRepository = postSitterRepository;
        this.petOwnerRepository = petOwnerRepository;
        this.webSocketNotificationService = webSocketNotificationService;
        this.persistentNotificationService = persistentNotificationService;
        this.reviewRepository = reviewRepository;
        this.postSitterService = postSitterService;
    }

    @Transactional
    public ReservationDto createReservation(Integer postSitterId, Integer petOwnerId, Double duration) {
        PostSitterEntity postSitter = postSitterRepository.findById(postSitterId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postSitterId));

        PetOwnerEntity petOwner = petOwnerRepository.findById(petOwnerId)
                .orElseThrow(() -> new RuntimeException("Pet Owner not found with ID: " + petOwnerId));

        double finalPrice;
        if (duration == null) {
            duration = 1.0;
        }
        finalPrice = switch (postSitter.getPricingModel()) {
            case PER_HOUR -> postSitter.getRatePerHour() * duration;
            case PER_DAY -> postSitter.getRatePerDay() * duration;
            case FLAT -> postSitter.getFlatRate();
        };

        ReservationEntity reservation = new ReservationEntity();
        reservation.setPostSitter(postSitter);
        reservation.setPetOwner(petOwner);
        reservation.setStatus("PENDING");
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setFinalPrice(finalPrice);

        ReservationEntity saved = reservationRepository.save(reservation);

        NotificationMessageDto notif = new NotificationMessageDto(
                "New Reservation Request",
                "You have received a new reservation request from " + petOwner.getFirstName() + " " + petOwner.getLastName()+ "."
        );
        Integer sitterId = postSitter.getPetSitter().getId();

        webSocketNotificationService.sendNotificationToSitter(notif, sitterId);
        persistentNotificationService.saveNotification(notif, sitterId, postSitterId);

        return Transformer.toDto(saved);
    }

    @Transactional
    public ReservationDto updateReservationStatus(Integer reservationId, String status) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with ID: " + reservationId));

        reservation.setStatus(status);
        reservation.setUpdatedAt(LocalDateTime.now());
        ReservationEntity updatedReservation = reservationRepository.save(reservation);

        PostSitterEntity postSitter = reservation.getPostSitter();
        if (postSitter != null) {
            if ("APPROVED".equalsIgnoreCase(status)) {
                postSitter.setStatus("APPROVED");
            } else if ("DENIED".equalsIgnoreCase(status)) {
                postSitter.setStatus("ACTIVE");
            }
            postSitterRepository.save(postSitter);
        }

        if ("APPROVED".equalsIgnoreCase(status) || "DENIED".equalsIgnoreCase(status)) {
            NotificationMessageDto notif = new NotificationMessageDto(
                    "Reservation Update",
                    "Your reservation status has been updated to: " + status
            );
            webSocketNotificationService.sendNotificationToOwner(notif);
            persistentNotificationService.saveOwnerNotification(notif, reservation.getPetOwner().getId(), postSitter.getId());
        }

        return Transformer.toDto(updatedReservation);
    }

    @Transactional(readOnly = true)
    public Page<ReservationDto> getReservationsForPetOwner(Integer petOwnerId, String status, String dateOrder, String sitterName, int page, int size) {
        Sort sort = Sort.by("createdAt");
        sort = "asc".equalsIgnoreCase(dateOrder) ? sort.ascending() : sort.descending();
        PageRequest pageable = PageRequest.of(page, size, sort);

        Page<ReservationEntity> reservationPage;
        if (status != null && !status.isEmpty() && sitterName != null && !sitterName.isEmpty()) {
            reservationPage = reservationRepository.findByPetOwnerIdAndStatusAndSitterNameContainingIgnoreCase(petOwnerId, status, sitterName, pageable);
        } else if (status != null && !status.isEmpty()) {
            reservationPage = reservationRepository.findByPetOwnerIdAndStatus(petOwnerId, status, pageable);
        } else if (sitterName != null && !sitterName.isEmpty()) {
            reservationPage = reservationRepository.findByPetOwnerIdAndSitterNameContainingIgnoreCase(petOwnerId, sitterName, pageable);
        } else {
            reservationPage = reservationRepository.findByPetOwnerId(petOwnerId, pageable);
        }
        return reservationPage.map(Transformer::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ReservationDto> getReservationsForPetSitter(Integer petSitterId, String status, String dateOrder, String petOwnerName, int page, int size) {
        Sort sort = Sort.by("createdAt");
        sort = "asc".equalsIgnoreCase(dateOrder) ? sort.ascending() : sort.descending();
        PageRequest pageable = PageRequest.of(page, size, sort);

        Page<ReservationEntity> reservationPage;
        if (status != null && !status.isEmpty() && petOwnerName != null && !petOwnerName.isEmpty()) {
            reservationPage = reservationRepository.findByPetSitterIdAndStatusAndPetOwnerNameContainingIgnoreCase(
                    petSitterId, status, petOwnerName, pageable
            );
        } else if (status != null && !status.isEmpty()) {
            reservationPage = reservationRepository.findByPetSitterIdAndStatus(petSitterId, status, pageable);
        } else if (petOwnerName != null && !petOwnerName.isEmpty()) {
            reservationPage = reservationRepository.findByPetSitterIdAndPetOwnerNameContainingIgnoreCase(
                    petSitterId, petOwnerName, pageable
            );
        } else {
            reservationPage = reservationRepository.findByPetSitterId(petSitterId, pageable);
        }
        return reservationPage.map(Transformer::toDto);
    }

    @Transactional(readOnly = true)
    public long getReservationCountForPetOwner(Integer petOwnerId) {
        return reservationRepository.countByPetOwnerId(petOwnerId);
    }

    public ReservationEntity getReservationById(Integer reservationId) {

        return reservationRepository.getReferenceById(reservationId);
    }

    public Integer getReservationByPostId(Integer postId) {

        return reservationRepository.findReservationIdByPostId(postId);
    }

    @Transactional
    public ReservationDto markCompleted(Integer reservationId, String role) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with ID: " + reservationId));

        if ("ROLE_PET_OWNER".equalsIgnoreCase(role)) {
            PostSitterEntity post = reservation.getPostSitter();
            if (post == null || post.getPostDate() == null || post.getAvailabilities() == null || post.getAvailabilities().isEmpty()) {
                throw new RuntimeException("Reservation's post data is incomplete. Cannot compute scheduled end time.");
            }

            LocalDateTime scheduledEnd = null;
            for (PostSitterAvailabilityEntity avail : post.getAvailabilities()) {
                LocalDateTime slotEnd = LocalDateTime.of(post.getPostDate(), avail.getEndTime());
                if (scheduledEnd == null || slotEnd.isAfter(scheduledEnd)) {
                    scheduledEnd = slotEnd;
                }
            }
            reservation.setOwnerMarkedComplete(true);
        } else if ("PET_SITTER".equalsIgnoreCase(role)) {
            reservation.setSitterMarkedComplete(true);
        } else {
            throw new RuntimeException("Unknown role: " + role);
        }

        if (reservation.isSitterMarkedComplete() && reservation.isOwnerMarkedComplete()) {
            reservation.setStatus("COMPLETED");
            reservation.setUpdatedAt(LocalDateTime.now());

            if (reservation.getPostSitter() != null) {
                postSitterService.updatePostStatus(reservation.getPostSitter().getId(), "COMPLETED");
            }
        } else {
            reservation.setStatus("AWAITING_COMPLETION_CONFIRMATION");
        }

        ReservationEntity updatedReservation = reservationRepository.save(reservation);
        return Transformer.toDto(updatedReservation);
    }


    @Transactional
    public ReviewDto submitReview(Integer reservationId, ReviewDto reviewDto) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with ID: " + reservationId));

        ReviewEntity review = new ReviewEntity();
        review.setReservation(reservation);
        review.setContent(reviewDto.getContent());
        review.setRating(reviewDto.getRating());
        review.setCreatedAt(LocalDateTime.now());
        review.setWrittenByRole(reviewDto.getWrittenByRole());
        review.setWrittenById(reviewDto.getWrittenById());
        review.setReviewedRole(reviewDto.getReviewedRole());
        review.setReviewedId(reviewDto.getReviewedId());
        review.setWrittenByFirstName(reviewDto.getWrittenByFirstName());
        review.setWrittenByLastName(reviewDto.getWrittenByLastName());

        ReviewEntity savedReview = reviewRepository.save(review);
        return Transformer.toReviewDto(savedReview);
    }

    @Transactional(readOnly = true)
    public long getReservationCountForPetOwnerByStatus(Integer petOwnerId, String status) {
        return reservationRepository.countByPetOwnerIdAndStatus(petOwnerId, status);
    }
}
