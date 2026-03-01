package com.mobili.backend.module.booking.booking.entity;

import com.mobili.backend.module.booking.ticket.entity.Ticket;
import com.mobili.backend.module.trip.entity.Trip;
import com.mobili.backend.module.user.entity.User;
import com.mobili.backend.shared.abstractEntity.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(nullable = false)
    private Integer numberOfSeats;

    @Column(nullable = false)
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    private LocalDateTime bookingDate;

    // --- AJOUT ICI ---
    @ElementCollection
    @CollectionTable(name = "booking_passenger_names", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "passenger_name")
    private List<String> passengerNames = new ArrayList<>();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<Ticket> tickets = new ArrayList<>();

    @PrePersist
    public void initBooking() {
        this.bookingDate = LocalDateTime.now();
        if (this.status == null) {
            this.status = BookingStatus.PENDING;
        }
        this.reference = "RESERVATION-" + System.currentTimeMillis() % 1000000;
    }
}