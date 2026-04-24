package com.mobili.backend.module.trip.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mobili.backend.module.trip.entity.Trip;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    @Query("SELECT t FROM Trip t JOIN FETCH t.partner LEFT JOIN FETCH t.station WHERE t.departureDateTime >= ?1 ORDER BY t.departureDateTime ASC")
    List<Trip> findAllUpcomingTrips(LocalDateTime startDateTime);

    @Query("SELECT t FROM Trip t LEFT JOIN FETCH t.partner WHERE t.id = ?1")
    Optional<Trip> findByIdWithPartner(Long id);

    @Query("SELECT t FROM Trip t LEFT JOIN FETCH t.partner LEFT JOIN FETCH t.station LEFT JOIN FETCH t.stops WHERE t.id = ?1")
    Optional<Trip> findByIdWithPartnerAndStops(Long id);

    @Query("SELECT t FROM Trip t LEFT JOIN FETCH t.partner")
    List<Trip> findAllWithPartner();

    @Query("SELECT COUNT(t) FROM Trip t WHERE t.partner.id = ?1")
    long countTripsByPartner(Long partnerId);

    @Query("SELECT COUNT(t) FROM Trip t WHERE t.partner.id = :partnerId AND t.station.id = :stationId")
    long countTripsByPartnerAndStation(@Param("partnerId") Long partnerId, @Param("stationId") Long stationId);

    @Query("SELECT t FROM Trip t LEFT JOIN FETCH t.partner LEFT JOIN FETCH t.station WHERE t.partner.id = ?1 ORDER BY t.departureDateTime DESC")
    List<Trip> findAllByPartnerId(Long partnerId);

    @Query("SELECT t FROM Trip t LEFT JOIN FETCH t.partner LEFT JOIN FETCH t.station WHERE t.partner.id = :partnerId AND t.station.id = :stationId ORDER BY t.departureDateTime DESC")
    List<Trip> findAllByPartnerIdAndStationId(@Param("partnerId") Long partnerId, @Param("stationId") Long stationId);
}
