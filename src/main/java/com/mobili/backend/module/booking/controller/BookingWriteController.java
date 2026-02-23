package com.mobili.backend.module.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mobili.backend.module.booking.service.BookingService;
import com.mobili.backend.module.booking.dto.BookingRequestDTO;
import com.mobili.backend.module.booking.dto.BookingResponseDTO;
import com.mobili.backend.module.booking.dto.mapper.BookingMapper;
import com.mobili.backend.module.booking.entity.Booking;


@RestController
@RequestMapping("/v1/bookings")
@RequiredArgsConstructor
public class BookingWriteController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDTO> create(@Valid @RequestBody BookingRequestDTO dto) {
        // Mapper le DTO vers l'entité
        Booking booking = BookingMapper.INSTANCE.toEntity(dto);

        // Exécuter la logique métier (décrémentation des places, etc.)
        Booking savedBooking = bookingService.createBooking(booking, dto.getTripId());

        // Retourner le ticket complet
        return new ResponseEntity<>(BookingMapper.INSTANCE.toDto(savedBooking), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }
}