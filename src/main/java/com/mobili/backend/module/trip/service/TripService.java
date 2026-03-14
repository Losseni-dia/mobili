package com.mobili.backend.module.trip.service;

import com.mobili.backend.infrastructure.security.authentication.UserPrincipal;
import com.mobili.backend.module.partner.entity.Partner;
import com.mobili.backend.module.partner.service.PartnerService;
import com.mobili.backend.module.trip.entity.Trip;
import com.mobili.backend.module.trip.entity.TripStatus;
import com.mobili.backend.module.trip.repository.TripRepository;
import com.mobili.backend.shared.mobiliError.exception.MobiliErrorCode;
import com.mobili.backend.shared.mobiliError.exception.MobiliException;
import com.mobili.backend.shared.sharedService.UploadService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final PartnerService partenaireService;
    private final UploadService uploadService;
   

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
        // 💡 On utilise la méthode avec le FETCH JOIN
        return tripRepository.findByIdWithPartner(id)
                .orElseThrow(() -> new MobiliException(
                        MobiliErrorCode.RESOURCE_NOT_FOUND,
                        "Voyage introuvable (ID: " + id + ")"));
    }

    @Transactional
    public Trip save(Trip trip, MultipartFile tripImage, UserPrincipal principal) {

        // 1. Sécurité : Récupération du partenaire (OK)
        Partner partner = partenaireService.findByOwnerId(principal.getUser().getId());
        if (partner == null) {
            throw new MobiliException(MobiliErrorCode.ACCESS_DENIED, "Compte partenaire introuvable");
        }
        trip.setPartner(partner);

        // 💡 LE FIX EST ICI : GESTION DE L'EXISTANT
        if (trip.getId() != null) {
            // On récupère le trajet actuel en base avant de le modifier
            Trip existingTrip = tripRepository.findById(trip.getId())
                    .orElseThrow(() -> new MobiliException(MobiliErrorCode.RESOURCE_NOT_FOUND, "Trajet introuvable"));

            // Si aucune nouvelle image n'est fournie, on REPREND l'ancienne URL
            if (tripImage == null || tripImage.isEmpty()) {
                trip.setVehicleImageUrl(existingTrip.getVehicleImageUrl());
            }

            // On peut aussi préserver le statut s'il n'est pas fourni dans le DTO
            if (trip.getStatus() == null) {
                trip.setStatus(existingTrip.getStatus());
            }
        } else {
            // CAS CRÉATION : Statut par défaut
            if (trip.getStatus() == null) {
                trip.setStatus(TripStatus.PROGRAMMÉ);
            }
        }

        // 2. Validation prix (OK)
        if (trip.getPrice() != null && trip.getPrice() < 0) {
            throw new MobiliException(MobiliErrorCode.VALIDATION_ERROR, "Le prix ne peut pas être négatif");
        }

        // 3. Traitement de la NOUVELLE image (si fournie)
        if (tripImage != null && !tripImage.isEmpty()) {
            String path = uploadService.saveImage(tripImage, "vehicles");
            trip.setVehicleImageUrl(path);
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