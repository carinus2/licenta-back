package com.start.pawpal_finder.service;

import com.start.pawpal_finder.Transformer;
import com.start.pawpal_finder.dto.NotificationMessageDto;
import com.start.pawpal_finder.dto.ReservationDto;
import com.start.pawpal_finder.entity.PostSitterEntity;
import com.start.pawpal_finder.entity.PetOwnerEntity;
import com.start.pawpal_finder.entity.ReservationEntity;
import com.start.pawpal_finder.repository.PostSitterRepository;
import com.start.pawpal_finder.repository.PetOwnerRepository;
import com.start.pawpal_finder.repository.ReservationRepository;
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
    private final PetOwnerRepository petOwnerRepository;
    private final WebSocketNotificationService webSocketNotificationService;
    private final PersistentNotificationService persistentNotificationService;

    public ReservationService(ReservationRepository reservationRepository,
                              PostSitterRepository postSitterRepository,
                              PetOwnerRepository petOwnerRepository,
                              WebSocketNotificationService webSocketNotificationService,
                              PersistentNotificationService persistentNotificationService) {
        this.reservationRepository = reservationRepository;
        this.postSitterRepository = postSitterRepository;
        this.petOwnerRepository = petOwnerRepository;
        this.webSocketNotificationService = webSocketNotificationService;
        this.persistentNotificationService = persistentNotificationService;
    }

    @Transactional
    public ReservationDto createReservation(Integer postSitterId, Integer petOwnerId, Double duration) {
        PostSitterEntity postSitter = postSitterRepository.findById(postSitterId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postSitterId));

        PetOwnerEntity petOwner = petOwnerRepository.findById(petOwnerId)
                .orElseThrow(() -> new RuntimeException("Pet Owner not found with ID: " + petOwnerId));

        double finalPrice = 0.0;
        if (duration == null) {
            duration = 1.0;
        }
        finalPrice = switch (postSitter.getPricingModel()) {
            case PER_HOUR -> postSitter.getRatePerHour() * duration;
            case PER_DAY -> postSitter.getRatePerDay() * duration;
            case FLAT -> postSitter.getFlatRate();
            default -> throw new RuntimeException("Unknown pricing model");
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
                "You have received a new reservation request for post ID " + postSitterId + "."
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
                postSitter.setStatus("Approved");
            } else if ("DENIED".equalsIgnoreCase(status)) {
                postSitter.setStatus("Active");
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
}
