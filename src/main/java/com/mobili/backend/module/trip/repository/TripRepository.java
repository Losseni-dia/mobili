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

        // On ajoute JOIN FETCH t.partner pour charger l'objet partenaire en une seule
        // requête
        @Query("SELECT t FROM Trip t JOIN FETCH t.partner " +
                        "WHERE LOWER(t.departureCity) = LOWER(:departure) " +
                        "AND LOWER(t.arrivalCity) = LOWER(:arrival) " +
                        "AND t.departureDateTime >= :startDateTime " +
                        "AND t.availableSeats > 0 " +
                        "ORDER BY t.departureDateTime ASC")
        List<Trip> searchTrips(
                        @Param("departure") String departure,
                        @Param("arrival") String arrival,
                        @Param("startDateTime") LocalDateTime startDateTime);

        // Ici aussi, le JOIN FETCH est indispensable pour le catalogue public
        @Query("SELECT t FROM Trip t JOIN FETCH t.partner " +
                        "WHERE t.departureDateTime >= :startDateTime " +
                        "AND t.availableSeats > 0 " +
                        "ORDER BY t.departureDateTime ASC")
        List<Trip> findAllUpcomingTrips(@Param("startDateTime") LocalDateTime startDateTime);
}