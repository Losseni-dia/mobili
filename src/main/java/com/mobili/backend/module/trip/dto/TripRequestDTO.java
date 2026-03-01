package com.mobili.backend.module.trip.dto;

import java.time.LocalDateTime;

import com.mobili.backend.module.trip.entity.VehicleType; // Import local

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripRequestDTO {

    private Long id;

    @NotNull(message = "L'ID du partenaire est obligatoire")
    private Long partnerId;

    @NotBlank(message = "La ville de départ est obligatoire")
    private String departureCity;

    @NotBlank(message = "La ville d'arrivée est obligatoire")
    private String arrivalCity;

    // Harmonisé avec le front-end et l'entité
    @NotBlank(message = "Le lieu d'embarquement est obligatoire")
    private String boardingPoint;

    @NotBlank(message = "Le numéro de plaque est obligatoire")
    private String vehiculePlateNumber;

    @NotNull(message = "Le type de véhicule est obligatoire")
    private VehicleType vehicleType;

    @NotNull(message = "La date et l'heure de départ sont obligatoires")
    private LocalDateTime departureDateTime;

    @NotNull(message = "Le prix est obligatoire")
    @Min(value = 0, message = "Le prix ne peut pas être négatif")
    private Double price;

    @NotNull(message = "Le nombre de places est obligatoire")
    @Min(value = 1, message = "Il doit y avoir au moins une place disponible")
    private Integer availableSeats;

    // Contiendra les villes d'arrêt (stops)
    private String moreInfo;
}