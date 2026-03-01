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
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    // On n'injecte pas le Mapper ici car le Service manipule généralement des
    // Entités.
    // C'est le Controller qui utilisera le Mapper pour transformer les DTOs.

    // --- RECHERCHE ---
    @Transactional(readOnly = true)
    public List<Trip> searchTrips(String departure, String arrival, LocalDate date) {
        LocalDateTime startSearch = (date != null)
                ? date.atStartOfDay()
                : LocalDateTime.now();

        return tripRepository.searchTrips(departure, arrival, startSearch);
    }

    // --- READ ---
    // Dans TripService.java
    @Transactional(readOnly = true)
    public List<Trip> findAllUpcoming() {
        // On récupère les trajets (avec ta marge de 5h pour être sûr de les voir)
        List<Trip> trips = tripRepository.findAllUpcomingTrips(LocalDateTime.now().minusHours(5));

        // ASTUCE : On "touche" l'objet partenaire pour forcer Hibernate à le charger
        // tant que la session est encore ouverte ici.
        trips.forEach(trip -> {
            if (trip.getPartner() != null) {
                trip.getPartner().getName(); // On force l'initialisation du proxy
            }
        });

        return trips;
    }

    @Transactional(readOnly = true)
    public Trip findById(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new MobiliException(
                        MobiliErrorCode.RESOURCE_NOT_FOUND,
                        "Voyage introuvable (ID: " + id + ")"));
    }

    // --- WRITE ---
    @Transactional
    public Trip save(Trip trip) {
        // Logique métier : Initialisation du statut par défaut si vide
        if (trip.getStatus() == null) {
            // Assure-toi que TripStatus.OPEN ou PLANNED existe dans ton Enum
            // trip.setStatus(TripStatus.OPEN);
        }

        // Logique métier : Sécurité sur le prix
        if (trip.getPrice() != null && trip.getPrice() < 0) {
            throw new MobiliException(MobiliErrorCode.VALIDATION_ERROR, "Le prix ne peut pas être négatif");
        }

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