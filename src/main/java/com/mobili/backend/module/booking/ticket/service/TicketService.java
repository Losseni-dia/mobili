package com.mobili.backend.module.booking.ticket.service;

import com.mobili.backend.module.booking.booking.entity.Booking;
import com.mobili.backend.module.booking.ticket.entity.Ticket;
import com.mobili.backend.module.booking.ticket.entity.TicketStatus;
import com.mobili.backend.module.booking.ticket.repository.TicketRepository;
import com.mobili.backend.module.trip.entity.Trip;
import com.mobili.backend.module.trip.repository.TripRepository;
import com.mobili.backend.module.trip.service.TripService;
import com.mobili.backend.module.user.entity.User;
import com.mobili.backend.module.user.service.UserService;
import com.mobili.backend.shared.MobiliError.exception.MobiliErrorCode;
import com.mobili.backend.shared.MobiliError.exception.MobiliException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TripService tripService;
    private final TripRepository tripRepository;
    private final UserService userService;

    @Transactional
    public Ticket create(Long tripId, Long userId) {
        Trip trip = tripService.findById(tripId);
        User user = userService.findById(userId);

        // Utilisation de ton code d'erreur spécifique TRP-001
        if (trip.getAvailableSeats() <= 0) {
            throw new MobiliException(
                    MobiliErrorCode.NO_SEATS_AVAILABLE,
                    "Désolé, toutes les places pour ce trajet ont été vendues.");
        }

        Ticket ticket = new Ticket();
        ticket.setTrip(trip);
        ticket.setPassenger(user);
        ticket.setAmountPaid(trip.getPrice());
        ticket.setStatus(TicketStatus.VALIDÉ);

        // Décrémentation
        trip.setAvailableSeats(trip.getAvailableSeats() - 1);
        tripRepository.save(trip);

        return ticketRepository.save(ticket);
    }

    @Transactional
    public void createFromBooking(Booking booking, String name, String seatNumber) {
        Ticket ticket = new Ticket();
        ticket.setBooking(booking);
        ticket.setTrip(booking.getTrip());
        ticket.setPassenger(booking.getCustomer());
        ticket.setPassengerName(name);
        ticket.setSeatNumber(seatNumber);
        ticket.setAmountPaid(booking.getTrip().getPrice());

        // GENERATION DU NUMERO DE TICKET UNIQUE
        // Exemple : MOB-ID_BOOKING-ALEATOIRE (ex: MOB-26-X8R)
        String uniqueCode = "MOB-" + booking.getId() + "-"
                + java.util.UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        ticket.setTicketNumber(uniqueCode);

        ticket.setBookingDate(LocalDateTime.now());
        ticket.setStatus(TicketStatus.VALIDÉ);

        ticketRepository.save(ticket);
        log.info("Ticket créé : {} | N°: {} | Siège: {}", name, uniqueCode, seatNumber);
    }

    @Transactional(readOnly = true)
    public List<Ticket> findAllByUserId(Long userId) {
        return ticketRepository.findAllByUserIdCustom(userId);
    }

    @Transactional
    public void cancelTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new MobiliException(MobiliErrorCode.RESOURCE_NOT_FOUND, "Ticket introuvable"));

        // Utilisation de ton code BKG-001
        if (ticket.getStatus() == TicketStatus.ANNULÉ) {
            throw new MobiliException(MobiliErrorCode.BOOKING_ALREADY_CANCELLED, "Ce ticket est déjà annulé.");
        }

        if (ticket.getStatus() == TicketStatus.VALIDÉ) {
            ticket.setStatus(TicketStatus.ANNULÉ);

            // On rend la place au voyage
            Trip trip = ticket.getTrip();
            trip.setAvailableSeats(trip.getAvailableSeats() + 1);
            tripRepository.save(trip);

            ticketRepository.save(ticket);
        }
    }

    @Transactional
    public Ticket verifyAndUseTicket(String ticketNumber) {
        // 1. Recherche du ticket
        Ticket ticket = ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new MobiliException(
                        MobiliErrorCode.RESOURCE_NOT_FOUND,
                        "Ticket invalide ou inexistant."));

        // 2. Vérification avec tes codes d'erreurs métier
        if (ticket.getStatus() == TicketStatus.UTILISÉ) {
            throw new MobiliException(
                    MobiliErrorCode.TICKET_ALREADY_USED,
                    "Alerte : Ce ticket a déjà été scanné à l'embarquement.");
        }

        if (ticket.getStatus() == TicketStatus.ANNULÉ) {
            throw new MobiliException(
                    MobiliErrorCode.TICKET_CANCELLED,
                    "Accès refusé : Ce ticket a été annulé par le client ou le système.");
        }
        if (ticket.getTrip().getDepartureDateTime().isBefore(LocalDateTime.now().minusHours(1))) {
            throw new MobiliException(
                    MobiliErrorCode.TICKET_EXPIRED,
                    "Ce ticket a expiré car la date du voyage est passée.");
        }

        // 3. Validation du passage
        ticket.setStatus(TicketStatus.UTILISÉ);
        return ticketRepository.save(ticket);
    }

    @Transactional(readOnly = true)
    public List<String> getOccupiedSeatsForTrip(Long tripId) {
        // Cette méthode doit appeler un nouveau findOccupiedSeats dans ton Repository
        return ticketRepository.findOccupiedSeatNumbersByTripId(tripId);
    }
}