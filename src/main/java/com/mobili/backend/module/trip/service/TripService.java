package com.mobili.backend.module.trip.service;

import com.mobili.backend.module.trip.entity.Trip;
import com.mobili.backend.module.trip.repository.TripRepository;
import com.mobili.backend.shared.exception.ResourceNotFoundException;
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
        // On définit le début de journée (00:00:00) et la fin (23:59:59)
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
                .orElseThrow(() -> new ResourceNotFoundException("Voyage introuvable (ID: " + id + ")"));
    }

    // --- WRITE ---
    @Transactional
    public Trip save(Trip trip) {
        // Ici on peut ajouter une logique métier :
        // ex: vérifier que le véhicule n'est pas déjà assigné à un autre trajet
        return tripRepository.save(trip);
    }

    @Transactional
    public void delete(Long id) {
        if (!tripRepository.existsById(id)) {
            throw new ResourceNotFoundException("Impossible de supprimer : Voyage inexistant");
        }
        tripRepository.deleteById(id);
    }
}