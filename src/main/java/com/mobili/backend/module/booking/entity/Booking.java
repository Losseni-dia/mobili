package com.mobili.backend.module.booking.entity;

import com.mobili.backend.module.trip.entity.Trip;
import com.mobili.backend.shared.abstractEntity.AbstractEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(nullable = false)
    private String passengerFirstname;

    @Column(nullable = false)
    private String passengerLastname;

    @Column(nullable = false)
    private String passengerPhone;

    private Integer seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(nullable = false)
    private Double totalAmount;
}