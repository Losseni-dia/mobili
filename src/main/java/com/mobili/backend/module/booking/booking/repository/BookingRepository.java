
package com.mobili.backend.module.booking.booking.repository;

import com.mobili.backend.module.booking.booking.entity.Booking;
import com.mobili.backend.module.booking.ticket.entity.Ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
        List<Booking> findByCustomerId(Long userId);

        List<Booking> findByTripId(Long tripId);

        @Query("SELECT DISTINCT b FROM Booking b " +
                        "JOIN FETCH b.trip " +
                        "JOIN FETCH b.customer " +
                        "LEFT JOIN FETCH b.passengerNames " +
                        "LEFT JOIN FETCH b.seatNumbers " +
                        "WHERE b.id = :id")
        Optional<Booking> findByIdWithDetails(@Param("id") Long id);

        @Query("SELECT DISTINCT b FROM Booking b " +
                        "LEFT JOIN FETCH b.seatNumbers " +
                        "WHERE b.trip.id = :tripId " +
                        "AND b.status != com.mobili.backend.module.booking.booking.entity.BookingStatus.CANCELLED")
        List<Booking> findByTripIdWithSeats(@Param("tripId") Long tripId);

      
}