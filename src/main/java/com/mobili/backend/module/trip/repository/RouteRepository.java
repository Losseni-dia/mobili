package com.mobili.backend.module.trip.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mobili.backend.module.trip.entity.Route;

import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    // Trouver une route spécifique entre deux villesOptional<Route>
    Optional<Route> findByDepartureCityIdAndArrivalCityId(Long departureId, Long arrivalId);
}