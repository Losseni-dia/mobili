package com.mobili.backend.module.transport.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class CompanyDTO {
    private Long id;

    @NotBlank(message = "Le nom de la compagnie est obligatoire")
    private String name;

    private String logoUrl;

    @NotBlank(message = "Le code pays est obligatoire")
    private String country;

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    private String phone;
}