package com.mobili.backend.module.booking.booking.service;

import com.mobili.backend.module.booking.booking.entity.Booking;
import com.mobili.backend.module.booking.booking.entity.BookingStatus;
import com.mobili.backend.module.booking.booking.repository.BookingRepository;
import com.mobili.backend.module.booking.ticket.service.TicketService;
import com.mobili.backend.module.trip.entity.Trip;
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

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TripService tripService;
    private final UserService userService;
    private final TicketService ticketService;

    @Transactional
    public Booking create(Long tripId, Long userId, List<String> passengerNames) {
        Trip trip = tripService.findById(tripId);
        User user = userService.findById(userId);
        int requestedSeats = passengerNames.size();

        // Vérification de sécurité
        if (trip.getAvailableSeats() < requestedSeats) {
            throw new MobiliException(MobiliErrorCode.NO_SEATS_AVAILABLE, "Places insuffisantes.");
        }

        Booking booking = new Booking();
        booking.setTrip(trip);
        booking.setCustomer(user);
        booking.setNumberOfSeats(requestedSeats);
        booking.setTotalPrice(trip.getPrice() * requestedSeats);
        booking.setStatus(BookingStatus.PENDING);

        // --- AJOUT CRUCIAL ICI ---
        booking.setPassengerNames(new ArrayList<>(passengerNames));

        // Mise à jour des places disponibles
        trip.setAvailableSeats(trip.getAvailableSeats() - requestedSeats);
        tripService.save(trip);

        return bookingRepository.save(booking);
    }

    @Transactional
    public void confirmPayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new MobiliException(MobiliErrorCode.RESOURCE_NOT_FOUND, "Réservation introuvable"));

        if (booking.getStatus() == BookingStatus.PENDING) {
            // 1. On valide la réservation
            booking.setStatus(BookingStatus.CONFIRMED);
            booking = bookingRepository.save(booking);

            // 2. GÉNÉRATION DES TICKETS NOMINATIFS
            // On parcourt la liste des noms stockés dans le booking
            for (String name : booking.getPassengerNames()) {
                // Maintenant les arguments correspondent : (Booking, String)
                ticketService.createFromBooking(booking, name);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Booking> findByUserId(Long userId) {
        return bookingRepository.findByCustomerId(userId);
    }
}