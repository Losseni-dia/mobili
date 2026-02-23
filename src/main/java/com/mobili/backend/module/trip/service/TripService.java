package com.mobili.backend.module.trip.service;

import com.mobili.backend.module.trip.entity.Trip;
import com.mobili.backend.module.trip.repository.TripRepository;
import com.mobili.backend.shared.MobiliError.exception.MobiliErrorCode;
import com.mobili.backend.shared.MobiliError.exception.MobiliException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;

    // --- RECHERCHE ---
    public List<Trip> searchTrips(String departure, String arrival, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return tripRepository.searchTrips(departure, arrival, startOfDay, endOfDay);
    }

    // --- READ ---
    public List<Trip> findAll() {
        return tripRepository.findAll();
    }

    public Trip findById(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new MobiliException(
                        MobiliErrorCode.RESOURCE_NOT_FOUND,
                        "Voyage introuvable (ID: " + id + ")"));
    }

    // --- WRITE ---
    @Transactional
    public Trip save(Trip trip) {
        return tripRepository.save(trip);
    }

    @Transactional
    public void delete(Long id) {
        if (!tripRepository.existsById(id)) {
            throw new MobiliException(
                    MobiliErrorCode.RESOURCE_NOT_FOUND,
                    "Impossible de supprimer : Voyage inexistant");
        }
        tripRepository.deleteById(id);
    }
}