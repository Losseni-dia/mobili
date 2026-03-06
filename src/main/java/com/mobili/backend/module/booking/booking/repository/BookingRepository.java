
package com.mobili.backend.module.booking.booking.repository;

import com.mobili.backend.module.booking.booking.entity.Booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomerId(Long userId);

    List<Booking> findByTripId(Long tripId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.trip " +
            "JOIN FETCH b.customer " + // ✅ On utilise le nom de la variable Java
            "WHERE b.id = :id")
    Optional<Booking> findByIdWithDetails(@Param("id") Long id);
}