package com.mobili.backend.module.booking.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.mobili.backend.module.booking.booking.dto.BookingRequestDTO;
import com.mobili.backend.module.booking.booking.dto.BookingResponseDTO;
import com.mobili.backend.module.booking.booking.dto.mapper.BookingMapper;
import com.mobili.backend.module.booking.booking.entity.Booking;
import com.mobili.backend.module.booking.booking.service.BookingService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    // CRÉER UNE RÉSERVATION (Statut PENDING par défaut)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDTO create(@RequestBody @Valid BookingRequestDTO dto) {
        // On extrait les données du DTO pour rester "pur" dans le service
        Booking booking = bookingService.create(
                dto.getTripId(),
                dto.getUserId(),
                dto.getPassengerNames());

        // On transforme l'entité en DTO pour la réponse
        return bookingMapper.toDto(booking);
    }

    // CONFIRMER LE PAIEMENT (Passe à CONFIRMED)
    @PatchMapping("/{id}/confirm")
    public void confirm(@PathVariable Long id) {
        bookingService.confirmPayment(id);
    }

    // RÉCUPÉRER L'HISTORIQUE DES RÉSERVATIONS D'UN CLIENT
    @GetMapping("/user/{userId}")
    public List<BookingResponseDTO> getByUserId(@PathVariable Long userId) {
        return bookingService.findByUserId(userId).stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }
}