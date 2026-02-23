package com.mobili.backend.module.transport.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CityDTO {
    private Long id;

    @NotBlank(message = "Le nom de la ville est obligatoire")
    private String cityName;
    private String country;
}