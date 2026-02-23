package com.mobili.backend.module.booking.dto;

import com.mobili.backend.module.booking.entity.BookingStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingResponseDTO {
    private Long id;
    private String passengerName;
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