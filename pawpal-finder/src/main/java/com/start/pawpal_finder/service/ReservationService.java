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
        switch (postSitter.getPricingModel()) {
            case PER_HOUR:
                finalPrice = postSitter.getRatePerHour() * duration;
                break;
            case PER_DAY:
                finalPrice = postSitter.getRatePerDay() * duration;
                break;
            case FLAT:
                finalPrice = postSitter.getFlatRate();
                break;
            default:
                throw new RuntimeException("Unknown pricing model");
        }

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
        persistentNotificationService.saveNotification(notif, sitterId);

        return Transformer.toDto(saved);
    }

    @Transactional
    public ReservationDto updateReservationStatus(Integer reservationId, String status) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with ID: " + reservationId));

        reservation.setStatus(status);
        reservation.setUpdatedAt(LocalDateTime.now());
        ReservationEntity updated = reservationRepository.save(reservation);

        if ("ACCEPTED".equals(status) || "DENIED".equals(status)) {
            NotificationMessageDto notif = new NotificationMessageDto(
                    "Reservation Update",
                    "Your reservation status has been updated to: " + status
            );
            webSocketNotificationService.sendNotificationToOwner(notif);
        }

        return Transformer.toDto(updated);
    }
}
