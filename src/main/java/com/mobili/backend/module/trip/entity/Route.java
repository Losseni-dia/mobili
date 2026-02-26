package com.mobili.backend.module.trip.entity;

import com.mobili.backend.shared.abstractEntity.AbstractEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "routes")
@Getter
@Setter
public class Route extends AbstractEntity {

    @ManyToOne(fetch = FetchType.EAGER) // ✅ Change LAZY en EAGER
    @JoinColumn(name = "departure_city_id", nullable = false)
    private City departureCity;

    @ManyToOne(fetch = FetchType.EAGER) // ✅ Change LAZY en EAGER
    @JoinColumn(name = "arrival_city_id", nullable = false)
    private City arrivalCity;

    private Double distanceKm;

    private Integer estimatedDurationMinutes;
}