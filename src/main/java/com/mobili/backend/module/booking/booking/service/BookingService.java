package com.mobili.backend.module.booking.booking.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mobili.backend.module.booking.booking.dto.BookingRequestDTO;
import com.mobili.backend.module.booking.booking.entity.Booking;
import com.mobili.backend.module.booking.booking.entity.BookingStatus;
import com.mobili.backend.module.booking.booking.repository.BookingRepository;
import com.mobili.backend.module.booking.ticket.service.TicketService;
import com.mobili.backend.module.trip.entity.Trip;
import com.mobili.backend.module.trip.repository.TripRepository;
import com.mobili.backend.module.trip.service.TripService;
import com.mobili.backend.module.user.entity.User;
import com.mobili.backend.module.user.repository.UserRepository;
import com.mobili.backend.module.user.service.UserService;
import com.mobili.backend.shared.mobiliError.exception.MobiliErrorCode;
import com.mobili.backend.shared.mobiliError.exception.MobiliException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TripService tripService;
    private final TripRepository tripRepository;
    private final UserService userService;
    private final TicketService ticketService;
    private final UserRepository userRepository;

    @Transactional
    public Booking create(BookingRequestDTO request) {
        // 1. Récupération des entités de base
        Trip trip = tripService.findById(request.getTripId());
        User user = userService.findById(request.getUserId());
        int requestedSeats = request.getNumberOfSeats();

        // 2. Vérification globale de la disponibilité (Quantité)
        if (trip.getAvailableSeats() < requestedSeats) {
            throw new MobiliException(MobiliErrorCode.NO_SEATS_AVAILABLE, "Places insuffisantes dans le bus.");
        }

        // 3. Vérification de l'unicité des sièges (Physique)
        List<String> takenSeats = ticketService.getOccupiedSeatsForTrip(request.getTripId());
        for (BookingRequestDTO.SeatSelectionDTO selection : request.getSelections()) {
            if (takenSeats.contains(selection.getSeatNumber())) {
                throw new MobiliException(MobiliErrorCode.VALIDATION_ERROR,
                        "Le siège " + selection.getSeatNumber() + " est déjà réservé.");
            }
        }

        // 4. Initialisation de la réservation (Booking)
        Booking booking = new Booking();
        booking.setTrip(trip);
        booking.setCustomer(user);
        booking.setNumberOfSeats(requestedSeats);
        booking.setTotalPrice(trip.getPrice() * requestedSeats);

        // ✅ CRUCIAL : On met le statut à PENDING pour permettre le paiement plus tard
        booking.setStatus(BookingStatus.PENDING);

        // 5. Extraction et stockage des infos passagers/sièges
        // On extrait les noms et numéros de sièges depuis le DTO pour les sauvegarder
        // dans le Booking
        List<String> names = request.getSelections().stream()
                .map(BookingRequestDTO.SeatSelectionDTO::getPassengerName)
                .toList();
        List<String> seats = request.getSelections().stream()
                .map(BookingRequestDTO.SeatSelectionDTO::getSeatNumber)
                .toList();

        booking.setPassengerNames(new HashSet<>(names));
        booking.setSeatNumbers(new HashSet<>(seats));

        // 6. Mise à jour physique des places du trajet (On bloque les places)
        trip.setAvailableSeats(trip.getAvailableSeats() - requestedSeats);
        tripRepository.save(trip);

        // 7. On sauvegarde et on retourne la réservation
        // (Note: Les TICKETS seront générés dans la méthode confirmPayment après le
        // débit)
        return bookingRepository.save(booking);
    }

    @Transactional
    public void confirmPayment(Long bookingId) {
        // 1. Récupération avec les détails (Jointures déjà optimisées)
        Booking booking = bookingRepository.findByIdWithDetails(bookingId)
                .orElseThrow(() -> new MobiliException(MobiliErrorCode.RESOURCE_NOT_FOUND, "Réservation introuvable"));

        // 2. Vérification du statut (Maintenant OK car Create s'arrête à PENDING)
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new MobiliException(MobiliErrorCode.VALIDATION_ERROR,
                    "Cette réservation est déjà confirmée ou annulée.");
        }

        // 3. LOGIQUE DE PAIEMENT (Wallet)
        User customer = booking.getCustomer();
        double amountToPay = booking.getTotalPrice();

        if (customer.getBalance() < amountToPay) {
            throw new MobiliException(MobiliErrorCode.INSUFFICIENT_BALANCE,
                    "Solde insuffisant dans votre portefeuille Mobili");
        }

        // Débit du solde
        customer.setBalance(customer.getBalance() - amountToPay);
        userRepository.save(customer);

        // 4. VALIDATION DE LA RÉSERVATION
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPaidAt(LocalDateTime.now());
        booking = bookingRepository.save(booking);

        // 5. GÉNÉRATION SÉCURISÉE DES TICKETS
        // Pour éviter que les noms et sièges ne se mélangent :
        List<String> names = new ArrayList<>(booking.getPassengerNames());
        List<String> seats = new ArrayList<>(booking.getSeatNumbers());

        // ✅ Optionnel mais recommandé : Tri pour garder une cohérence si les listes
        // ont été créées dans l'ordre alphabétique
        Collections.sort(names);
        Collections.sort(seats);

        for (int i = 0; i < names.size(); i++) {
            // Cette méthode crée le ticket physique que Maya scannera
            ticketService.createFromBooking(booking, names.get(i), seats.get(i));
        }

        log.info("💰 Paiement réussi - Réservation: {} - Client: {}", booking.getId(), customer.getEmail());
    }

    @Transactional(readOnly = true)
    public List<Booking> findByUserId(Long userId) {
        return bookingRepository.findByCustomerId(userId);
    }

    @Transactional(readOnly = true)
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<String> getOccupiedSeatNumbers(Long tripId) {
        // 1. On récupère tout avec le FETCH déjà fait
        List<Booking> bookings = bookingRepository.findByTripIdWithSeats(tripId);

        // 2. On "aplatit" les Sets de sièges en une seule liste de Strings
        return bookings.stream()
                .flatMap(booking -> booking.getSeatNumbers().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Booking findById(Long id) {
        // On utilise la méthode avec JOIN FETCH pour charger trip et customer
        return bookingRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new MobiliException(MobiliErrorCode.RESOURCE_NOT_FOUND, "Réservation introuvable"));
    }
}