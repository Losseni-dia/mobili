package com.mobili.backend.module.trip.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripRequestDTO {

    private Long id;

    @NotNull(message = "La route est obligatoire")
    private Long routeId;

    @NotNull(message = "Le véhicule est obligatoire")
    private Long vehicleId;

    @NotNull(message = "La date et l'heure de départ sont obligatoires")
    @FutureOrPresent(message = "Le départ ne peut pas être dans le passé")
    private LocalDateTime departureDateTime;

    @NotNull(message = "Le prix est obligatoire")
    @Min(value = 0, message = "Le prix ne peut pas être négatif")
    private Double price;

    @NotNull(message = "Le nombre de places est obligatoire")
    @Min(value = 1, message = "Il doit y avoir au moins une place disponible")
    private Integer availableSeats;
}