package com.mobili.backend.module.transport.dto;

import lombok.Data;

@Data
public class VehicleDTO {
    private Long id;
    private String plateNumber;
    private String model;
    private String imageUrl;
    private Integer capacity;
    private Boolean available;
    private String type; // BUS, MINIBUS, etc.
    private Long companyId; // On lie par l'ID pour simplifier le JSON
    private String companyName; // Pratique pour l'affichage direct
}