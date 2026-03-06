package com.mobili.backend.module.booking.booking.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mobili.backend.module.booking.booking.dto.BookingRequestDTO;
import com.mobili.backend.module.booking.booking.dto.BookingResponseDTO;
import com.mobili.backend.module.booking.booking.dto.mapper.BookingMapper;
import com.mobili.backend.module.booking.booking.entity.Booking;
import com.mobili.backend.module.booking.booking.service.BookingService;
import com.mobili.backend.module.user.entity.User;
import com.mobili.backend.module.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;
    private final UserService userService;

    // CRÉER UNE RÉSERVATION (Statut PENDING par défaut)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public BookingResponseDTO create(@RequestBody @Valid BookingRequestDTO dto, Principal principal) {
        // 1. On récupère le login (email ou username) de celui qui est connecté via le
        // JWT
        String currentUserLogin = principal.getName();

        // 2. On récupère l'entité User correspondante
        User user = userService.findByLogin(currentUserLogin);

        // 3. Extraction des listes (Nom + Sièges)
        List<String> names = dto.getSelections().stream()
                .map(BookingRequestDTO.SeatSelectionDTO::getPassengerName)
                .toList();
        List<String> seats = dto.getSelections().stream()
                .map(BookingRequestDTO.SeatSelectionDTO::getSeatNumber)
                .toList();

        // 4. On passe l'ID de l'utilisateur CONNECTÉ au service, pas celui du DTO
        Booking booking = bookingService.create(
                dto.getTripId(),
                user.getId(),
                names,
                seats);

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

    @GetMapping("/{id}")
    public BookingResponseDTO getById(@PathVariable Long id) {
        Booking booking = bookingService.findById(id);
        return bookingMapper.toDto(booking);
    }

    @GetMapping
    public List<BookingResponseDTO> getAll() {
        return bookingService.findAll().stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/trips/{tripId}/occupied-seats")
    public List<String> getOccupiedSeats(@PathVariable Long tripId) {
        List<String> seats = bookingService.getOccupiedSeatNumbers(tripId);
        return seats != null ? seats : new ArrayList<>();
    }
}