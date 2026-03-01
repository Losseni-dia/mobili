package com.mobili.backend.module.booking.booking.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {

    @NotNull(message = "L'ID du voyage est obligatoire")
    private Long tripId;

    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private Long userId;

    @NotEmpty(message = "La liste des passagers ne peut pas être vide")
    private List<String> passengerNames;

    @NotNull(message = "Le nombre de places est obligatoire")
    @Min(value = 1, message = "Vous devez réserver au moins une place")
    private Integer numberOfSeats;

    // Le prix total sera calculé côté serveur pour éviter les fraudes
}