package com.mobili.backend.module.trip.entity;

import com.mobili.backend.module.transport.entity.Vehicle;
import com.mobili.backend.shared.abstractEntity.AbstractEntity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trips")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trip extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Route route; // L'itinéraire (ex: Abidjan -> Bamako)

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle; // Le bus affecté à ce trajet

    @Column(nullable = false)
    private LocalDateTime departureDateTime; // Date et heure de départ

    @Column(nullable = false)
    private LocalDateTime arrivalDateTime; // Date et heure d'arrivée estimée

    @Column(nullable = false)
    private Double price; // Prix final pour ce départ spécifique

    @Column(nullable = false)
    private Integer availableSeats; // Nombre de places encore libres

    @Enumerated(EnumType.STRING)
    private TripStatus status; // PROGRAMMÉ, EN COURS, TERMINÉ, ANNULÉ
}