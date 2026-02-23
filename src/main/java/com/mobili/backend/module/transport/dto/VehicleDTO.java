package com.mobili.backend.module.transport.dto;

import lombok.Data;

@Data
public class VehicleDTO {
    private Long id;
    private String plateNumber;
    private Integer capacity;
    private String type; // On le passe en String pour le JSON
}