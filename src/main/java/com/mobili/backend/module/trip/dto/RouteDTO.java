package com.mobili.backend.module.trip.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteDTO {

    private Long id;

    @NotNull(message = "La ville de départ est obligatoire")
    private Long departureCityId;

    @NotNull(message = "La ville d'arrivée est obligatoire")
    private Long arrivalCityId;

    private String departureCityName;
    private String arrivalCityName;

    @Positive(message = "La distance doit être positive")
    private Double distanceKm;

    @Positive(message = "La durée estimée doit être positive")
    private Integer estimatedDurationMinutes;
}