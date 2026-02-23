package com.mobili.backend.module.booking.controller;

import com.mobili.backend.module.booking.dto.BookingResponseDTO;
import com.mobili.backend.module.booking.dto.mapper.BookingMapper;
import com.mobili.backend.module.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingReadController {

    private final BookingService bookingService;

    @GetMapping
    public List<BookingResponseDTO> getAll() {
        return bookingService.findAll().stream()
                .map(BookingMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public BookingResponseDTO getById(@PathVariable Long id) {
        return BookingMapper.INSTANCE.toDto(bookingService.findById(id));
    }

    // Très utile pour le Front : retrouver ses tickets par téléphone
    @GetMapping("/my-tickets")
    public List<BookingResponseDTO> getByPhone(@RequestParam String phone) {
        // Tu peux ajouter cette méthode dans ton BookingService
        // Elle appellera le repository : findByPassengerPhoneOrderByCreatedAtDesc
        return bookingService.findByPhone(phone).stream()
                .map(BookingMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }
}