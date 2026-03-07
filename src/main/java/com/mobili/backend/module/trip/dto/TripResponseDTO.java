package com.mobili.backend.module.trip.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TripResponseDTO {
    private Long id;
    private String partnerName;
    private String departureCity;
    private String arrivalCity;

    // On garde boardingPoint pour être cohérent avec l'entité et le front
    private String boardingPoint;

    private String vehiculePlateNumber;
    private String vehicleImageUrl;
    private String vehicleType; // Sera envoyé en String (ex: "BUS_CLIMATISE")
    private LocalDateTime departureDateTime;
    private Double price;
    private Integer availableSeats;
    private String status;
    // Contiendra tes villes d'arrêt/étapes (stops_cities en DB)
    private String moreInfo;
}