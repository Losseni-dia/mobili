package com.mobili.backend.module.trip.repository;

import com.mobili.backend.module.trip.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    // Recherche multicritères : Ville départ, Arrivée, et Date
    @Query("SELECT t FROM Trip t WHERE t.route.departureCity.cityName = :departure " +
            "AND t.route.arrivalCity.cityName = :arrival " +
            "AND t.departureDateTime >= :startOfDay " +
            "AND t.departureDateTime <= :endOfDay " +
            "AND t.availableSeats > 0")
    List<Trip> searchTrips(
            @Param("departure") String departure,
            @Param("arrival") String arrival,
            @Param("startOfDay") LocalDateTime start,
            @Param("endOfDay") LocalDateTime end);
}