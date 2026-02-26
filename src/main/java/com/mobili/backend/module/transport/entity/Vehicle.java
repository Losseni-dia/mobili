package com.mobili.backend.module.transport.entity;

import com.mobili.backend.shared.abstractEntity.AbstractEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String plateNumber; // Plaque d'immatriculation (unique)

    private String model; // Ex: Mercedes-Benz, Toyota Coaster

    private Integer capacity; // Nombre total de sièges (ex: 70)

    private Boolean available; // Disponibilité du véhicule

    @Enumerated(EnumType.STRING)
    private VehicleType type; // BUS, MINIBUS, CAR

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company; // Le propriétaire du véhicule
}