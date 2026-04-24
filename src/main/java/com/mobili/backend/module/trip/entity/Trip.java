package com.mobili.backend.module.trip.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mobili.backend.module.partner.entity.Partner;
import com.mobili.backend.module.station.entity.Station;
import com.mobili.backend.shared.abstractEntity.AbstractEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trips")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trip extends AbstractEntity {

    @Column(name = "departure_city", nullable = false)
    private String departureCity;

    @Column(name = "arrival_city", nullable = false)
    private String arrivalCity;

    @Column(name = "boarding_point") // Match avec le boardingPoint du front
    private String boardingPoint;

    @Column(name = "vehicle_plate_number", nullable = false)
    private String vehiculePlateNumber;

    @Column(name = "vehicle_image_url")
    private String vehicleImageUrl;

    @Column(name = "departure_date_time", nullable = false)
    private LocalDateTime departureDateTime;

    @Column(name = "price", nullable = false)
    private Double price;

    /**
     * Tarif pour un parcours du premier au dernier arrêt, indépendant de la somme des tronçons
     * consécutifs (prix d’embarquement / promotion).
     */
    @Column(name = "origin_destination_price")
    private Double originDestinationPrice;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TripStatus status;

    @Column(name = "stops_cities", columnDefinition = "TEXT")
    private String moreInfo;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    /** Gare qui a publié / porte l’offre (segmentation statistique et périmètre gare) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    private Station station;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stopIndex ASC")
    private List<TripStop> stops = new ArrayList<>();
}