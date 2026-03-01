package com.mobili.backend.module.booking.booking.dto;


import lombok.Data;
import java.time.LocalDateTime;

import com.mobili.backend.module.booking.booking.entity.BookingStatus;

@Data
public class BookingResponseDTO {
    private Long id;
    private String reference;
    private String departureCity;
    private String arrivalCity;
    private LocalDateTime departureDateTime;
    private Integer numberOfSeats;
    private Double totalPrice;
    private BookingStatus status;
    private LocalDateTime bookingDate;
}