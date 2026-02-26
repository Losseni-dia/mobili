package com.mobili.backend.module.trip.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TripResponseDTO {
    private Long id;
    private String departureCityName; // "Abidjan"
    private String arrivalCityName; // "Bamako"
    private String companyName; // "Sitarail"
    private String companyLogo;
    private LocalDateTime departureDateTime;
    private Double price;
    private Integer availableSeats;
    private String vehicleType; // "BUS VIP"
    private String status;
}