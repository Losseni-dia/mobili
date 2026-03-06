package com.mobili.backend.module.booking.booking.service;

import com.mobili.backend.module.booking.booking.entity.Booking;
import com.mobili.backend.module.booking.booking.entity.BookingStatus;
import com.mobili.backend.module.booking.booking.repository.BookingRepository;
import com.mobili.backend.module.booking.ticket.service.TicketService;
import com.mobili.backend.module.trip.entity.Trip;
import com.mobili.backend.module.trip.repository.TripRepository;
import com.mobili.backend.module.trip.service.TripService;
import com.mobili.backend.module.user.entity.User;
import com.mobili.backend.module.user.service.UserService;
import com.mobili.backend.shared.mobiliError.exception.MobiliErrorCode;
import com.mobili.backend.shared.mobiliError.exception.MobiliException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TripService tripService;
    private final TripRepository tripRepository;
    private final UserService userService;
    private final TicketService ticketService;

    @Transactional
    public Booking create(Long tripId, Long userId, List<String> passengerNames, List<String> seatNumbers) {
        // 1. Récupération des entités
        Trip trip = tripService.findById(tripId);
        User user = userService.findById(userId);
        int requestedSeats = passengerNames.size();

        // 2. Vérification des places disponibles (quantité)
        if (trip.getAvailableSeats() < requestedSeats) {
            throw new MobiliException(MobiliErrorCode.NO_SEATS_AVAILABLE, "Places insuffisantes.");
        }

        // 3. Vérification des sièges spécifiques (unicité physique)
        // On appelle le TicketService pour savoir quels sièges sont déjà pris sur ce
        // trajet
        List<String> takenSeats = ticketService.getOccupiedSeatsForTrip(tripId);
        for (String seat : seatNumbers) {
            if (takenSeats.contains(seat)) {
                throw new MobiliException(MobiliErrorCode.VALIDATION_ERROR, "Le siège " + seat + " est déjà occupé.");
            }
        }

        // 4. Création de la réservation
        Booking booking = new Booking();
        booking.setTrip(trip);
        booking.setCustomer(user);
        booking.setNumberOfSeats(requestedSeats);
        booking.setTotalPrice(trip.getPrice() * requestedSeats);
        booking.setStatus(BookingStatus.PENDING);
        booking.setPassengerNames(new ArrayList<>(passengerNames));
        booking.setSeatNumbers(new ArrayList<>(seatNumbers));

        // On verra dans le Ticket comment stocker ces seatNumbers plus tard

        // 5. Mise à jour du trajet
        trip.setAvailableSeats(trip.getAvailableSeats() - requestedSeats);
        tripRepository.save(trip);

        return bookingRepository.save(booking);
    }

    @Transactional
    public void confirmPayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new MobiliException(MobiliErrorCode.RESOURCE_NOT_FOUND, "Réservation introuvable"));

        if (booking.getStatus() == BookingStatus.PENDING) {
            // 1. Validation de la réservation
            booking.setStatus(BookingStatus.CONFIRMED);
            booking = bookingRepository.save(booking);

            // 2. GÉNÉRATION DES TICKETS (Nom + Siège)
            // On récupère les deux listes qui ont la même taille
            List<String> names = booking.getPassengerNames();
            List<String> seats = booking.getSeatNumbers();

            for (int i = 0; i < names.size(); i++) {
                // On extrait les valeurs par index
                String currentName = names.get(i);
                String currentSeat = seats.get(i);

                // ✅ Maintenant les arguments correspondent à ta méthode TicketService
                ticketService.createFromBooking(booking, currentName, currentSeat);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Booking> findByUserId(Long userId) {
        return bookingRepository.findByCustomerId(userId);
    }


    @Transactional(readOnly = true)
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    public List<String> getOccupiedSeatNumbers(Long tripId) {
        // 1. Récupérer toutes les réservations pour ce trajet
        List<Booking> bookings = bookingRepository.findByTripId(tripId);

        // 2. Extraire et "aplatir" tous les numéros de sièges
        return bookings.stream()
                // On ne prend que les réservations CONFIRMED ou PENDING (selon ta logique)
                .filter(b -> b.getStatus() != BookingStatus.CANCELLED)
                // flatMap transforme List<List<String>> en une seule List<String>
                .flatMap(booking -> booking.getSeatNumbers().stream())
                .distinct() // Sécurité : éviter les doublons au cas où
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Booking findById(Long id) {
        // On utilise la méthode avec JOIN FETCH pour charger trip et customer
        return bookingRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new MobiliException(MobiliErrorCode.RESOURCE_NOT_FOUND, "Réservation introuvable"));
    }
}