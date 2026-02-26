package com.mobili.backend.module.trip.service;

import com.mobili.backend.module.trip.entity.Trip;
import com.mobili.backend.module.trip.repository.TripRepository;
import com.mobili.backend.shared.mobiliError.exception.MobiliErrorCode;
import com.mobili.backend.shared.mobiliError.exception.MobiliException;


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
    @Transactional(readOnly = true)
    public List<Trip> searchTrips(String departure, String arrival, LocalDate date) {
        // Si l'utilisateur ne donne pas de date, on prend "Maintenant" (Heure précise)
        // Si l'utilisateur donne une date, on prend le début de cette journée là
        // (00:00)
        LocalDateTime startSearch = (date != null)
                ? date.atStartOfDay()
                : LocalDateTime.now();

        // On appelle le repo avec exactement 3 arguments
        return tripRepository.searchTrips(departure, arrival, startSearch);
    }

    // --- READ ---
    public List<Trip> findAllUpcoming() {
        // On récupère la date/heure actuelle pour ne pas montrer de trajets périmés
        LocalDateTime now = LocalDateTime.now();
        return tripRepository.findAllUpcomingTrips(now);
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