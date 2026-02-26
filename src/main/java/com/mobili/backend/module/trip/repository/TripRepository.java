package com.mobili.backend.module.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mobili.backend.module.trip.entity.Trip;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    @Query("SELECT t FROM Trip t WHERE LOWER(t.route.departureCity.cityName) = LOWER(:departure) " +
           "AND LOWER(t.route.arrivalCity.cityName) = LOWER(:arrival) " +
           "AND t.departureDateTime >= :startDateTime " + // On cherche à partir de cette date
           "AND t.availableSeats > 0 " +
           "ORDER BY t.departureDateTime ASC")
    List<Trip> searchTrips(
            @Param("departure") String departure,
            @Param("arrival") String arrival,
            @Param("startDateTime") LocalDateTime startDateTime);

            @Query("SELECT t FROM Trip t WHERE t.departureDateTime >= :startDateTime " +
            "AND t.availableSeats > 0 " +
            "ORDER BY t.departureDateTime ASC")
    List<Trip> findAllUpcomingTrips(@Param("startDateTime") LocalDateTime startDateTime);
}