package com.mobili.backend.module.booking.dto;

import lombok.Data;
import java.time.LocalDateTime;

import com.mobili.backend.module.booking.entity.BookingStatus;;

@Data
public class BookingResponseDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String passengerPhone;
    private Integer seatNumber;
    private BookingStatus status;
    private Double totalAmount;

    // Infos du voyage pour le récapitulatif
    private String departureCity;
    private String arrivalCity;
    private LocalDateTime departureDateTime;
    private String companyName;
}