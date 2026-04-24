package com.mobili.backend.module.trip.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class TripResponseDTO {
    private Long id;
    /** Gare d’où l’offre est portée (stats / périmètre). */
    private Long stationId;
    private String stationName;
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
    /** Tarif direct départ final → arrivée finale (peut différer de la somme des tronçons). */
    private Double originDestinationPrice;
    private Integer totalSeats;
    private Integer availableSeats;
    private String status;
    // Contiendra tes villes d'arrêt/étapes (stops_cities en DB)
    private String moreInfo;

    /** Tronçons consécutifs avec prix (vide si tarification au prorata uniquement). */
    private List<TripLegFareResponse> legFares;
}