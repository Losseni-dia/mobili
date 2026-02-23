package com.mobili.backend.module.booking.service;

import com.mobili.backend.module.booking.entity.Booking;
import com.mobili.backend.module.booking.entity.BookingStatus;
import com.mobili.backend.module.booking.repository.BookingRepository;
import com.mobili.backend.module.trip.entity.Trip;
import com.mobili.backend.module.trip.service.TripService;
import com.mobili.backend.shared.MobiliError.exception.MobiliErrorCode;
import com.mobili.backend.shared.MobiliError.exception.MobiliException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TripService tripService;

    @Transactional
    public Booking createBooking(Booking booking, Long tripId) {
        // 1. Récupérer le voyage (lance déjà une MobiliException si non trouvé)
        Trip trip = tripService.findById(tripId);

        // 2. Vérifier la disponibilité des places
        if (trip.getAvailableSeats() <= 0) {
            throw new MobiliException(MobiliErrorCode.NO_SEATS_AVAILABLE);
        }

        // 3. Mettre à jour le stock de places du voyage
        trip.setAvailableSeats(trip.getAvailableSeats() - 1);
        tripService.save(trip);

        // 4. Initialiser les données de la réservation
        booking.setTrip(trip);
        booking.setTotalAmount(trip.getPrice());
        booking.setStatus(BookingStatus.CONFIRMED); // On confirme directement pour l'instant

        return bookingRepository.save(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new MobiliException(MobiliErrorCode.RESOURCE_NOT_FOUND, "Réservation introuvable"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new MobiliException(MobiliErrorCode.BOOKING_ALREADY_CANCELLED);
        }

        // Rendre la place au voyage
        Trip trip = booking.getTrip();
        trip.setAvailableSeats(trip.getAvailableSeats() + 1);
        tripService.save(trip);

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    public Booking findById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new MobiliException(MobiliErrorCode.RESOURCE_NOT_FOUND,
                        "Réservation #" + id + " inexistante"));
    }
}