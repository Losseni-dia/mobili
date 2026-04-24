package com.mobili.backend.module.trip.service;

import com.mobili.backend.infrastructure.security.authentication.UserPrincipal;
import com.mobili.backend.module.analytics.entity.AnalyticsEventType;
import com.mobili.backend.module.analytics.service.AnalyticsEventService;
import org.springframework.security.core.context.SecurityContextHolder;

import com.mobili.backend.module.partner.entity.Partner;
import com.mobili.backend.module.partner.service.PartnerService;
import com.mobili.backend.module.station.entity.Station;
import com.mobili.backend.module.station.service.StationService;
import com.mobili.backend.module.trip.dto.TripLegFareResponse;
import com.mobili.backend.module.trip.dto.TripLegFareRequest;
import com.mobili.backend.module.trip.dto.TripPricePreviewRequest;
import com.mobili.backend.module.trip.dto.TripPricePreviewResponse;
import com.mobili.backend.module.trip.dto.TripStopResponseDTO;
import com.mobili.backend.module.trip.entity.Trip;
import com.mobili.backend.module.trip.entity.TripStatus;
import com.mobili.backend.module.trip.entity.VehicleType;
import com.mobili.backend.module.trip.repository.TripRepository;
import com.mobili.backend.shared.MobiliError.exception.MobiliErrorCode;
import com.mobili.backend.shared.MobiliError.exception.MobiliException;
import com.mobili.backend.shared.sharedService.UploadService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final PartnerService partenaireService;
    private final UploadService uploadService;
    private final TripStopSyncService tripStopSyncService;
    private final TripRunService tripRunService;
    private final TripPricingService tripPricingService;
    private final AnalyticsEventService analyticsEventService;
    private final StationService stationService;

    @Transactional(readOnly = true)
    public List<Trip> findMyTrips() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal up)) {
            throw new MobiliException(MobiliErrorCode.ACCESS_DENIED, "Non authentifié");
        }
        Partner partner = partenaireService.getCurrentPartner();
        if (up.getStationId() != null) {
            return tripRepository.findAllByPartnerIdAndStationId(partner.getId(), up.getStationId());
        }
        return tripRepository.findAllByPartnerId(partner.getId());
    }

    // --- RECHERCHE (terminus + étapes dans moreInfo, ordre : départ → étapes CSV → arrivée) ---
    @Transactional(readOnly = true)
    public List<Trip> searchTrips(String departure, String arrival, LocalDate date) {
        LocalDateTime startSearch = (date != null)
                ? date.atStartOfDay()
                : LocalDateTime.now();

        List<Trip> candidates = tripRepository.findAllUpcomingTrips(startSearch);
        String dep = normalizeQuery(departure);
        String arr = normalizeQuery(arrival);

        if (dep == null && arr == null) {
            return candidates;
        }

        return candidates.stream()
                .filter(t -> matchesRouteSearch(t, dep, arr))
                .collect(Collectors.toList());
    }

    /**
     * Chaîne ordonnée des villes : departureCity, segments de moreInfo (séparés par
     * virgule), arrivalCity.
     * Convention : moreInfo = villes intermédiaires uniquement (sans répéter départ
     * / arrivée).
     */
    List<String> buildCityChain(Trip trip) {
        List<String> chain = new ArrayList<>();
        chain.add(normalizeCityToken(trip.getDepartureCity()));
        if (trip.getMoreInfo() != null && !trip.getMoreInfo().isBlank()) {
            for (String part : trip.getMoreInfo().split(",")) {
                String token = normalizeCityToken(part);
                if (!token.isEmpty() && !chain.get(chain.size() - 1).equals(token)) {
                    chain.add(token);
                }
            }
        }
        String arrivalToken = normalizeCityToken(trip.getArrivalCity());
        if (chain.isEmpty() || !chain.get(chain.size() - 1).equals(arrivalToken)) {
            chain.add(arrivalToken);
        }
        return chain;
    }

    private boolean matchesRouteSearch(Trip trip, String depQuery, String arrQuery) {
        List<String> chain = buildCityChain(trip);
        if (depQuery != null && arrQuery != null) {
            return hasValidSegment(chain, depQuery, arrQuery);
        }
        if (depQuery != null) {
            return chain.stream().anyMatch(city -> partialCityMatch(city, depQuery));
        }
        return chain.stream().anyMatch(city -> partialCityMatch(city, arrQuery));
    }

    /** Il existe i &lt; j avec départ et arrivée recherchés sur ces positions (préfixe insensible à la casse). */
    private boolean hasValidSegment(List<String> chain, String depQuery, String arrQuery) {
        for (int i = 0; i < chain.size(); i++) {
            if (!partialCityMatch(chain.get(i), depQuery)) {
                continue;
            }
            for (int j = i + 1; j < chain.size(); j++) {
                if (partialCityMatch(chain.get(j), arrQuery)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean partialCityMatch(String cityNorm, String queryNorm) {
        if (queryNorm == null || queryNorm.isEmpty()) {
            return true;
        }
        return cityNorm.startsWith(queryNorm);
    }

    private static String normalizeCityToken(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.trim().toLowerCase(Locale.ROOT);
    }

    private static String normalizeQuery(String raw) {
        if (raw == null) {
            return null;
        }
        String t = raw.trim();
        return t.isEmpty() ? null : t.toLowerCase(Locale.ROOT);
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

    @Transactional
    public Trip findById(Long id) {
        Trip trip = tripRepository.findByIdWithPartnerAndStops(id)
                .orElseThrow(() -> new MobiliException(
                        MobiliErrorCode.RESOURCE_NOT_FOUND,
                        "Voyage introuvable (ID: " + id + ")"));
        if (trip.getStops() == null || trip.getStops().isEmpty()) {
            tripStopSyncService.syncStopsForTrip(trip);
            trip = tripRepository.save(trip);
        }
        if (trip.getPartner() != null) {
            trip.getPartner().getName();
        }
        return trip;
    }

    @Transactional(readOnly = true)
    public List<TripLegFareResponse> listLegFares(Long tripId) {
        return tripPricingService.listLegFareResponses(tripId);
    }

    @Transactional
    public Trip save(
            Trip trip,
            MultipartFile tripImage,
            UserPrincipal principal,
            List<TripLegFareRequest> legFares,
            Long stationIdFromDto) {
        final boolean isNew = trip.getId() == null;
        Partner partner = partenaireService.getCurrentPartner();
        trip.setPartner(partner);
        if (trip.getId() != null) {
            Trip existingTrip = tripRepository.findById(trip.getId())
                    .orElseThrow(() -> new MobiliException(MobiliErrorCode.RESOURCE_NOT_FOUND, "Trajet introuvable"));
            assertTripWriteAccess(existingTrip, principal, partner);
            if (tripImage == null || tripImage.isEmpty()) {
                trip.setVehicleImageUrl(existingTrip.getVehicleImageUrl());
            }
            if (trip.getStatus() == null) {
                trip.setStatus(existingTrip.getStatus());
            }
            applyStationOnWrite(trip, principal, partner, stationIdFromDto, existingTrip);
        } else {
            if (trip.getStatus() == null) {
                trip.setStatus(TripStatus.PROGRAMMÉ);
            }
            applyStationOnWrite(trip, principal, partner, stationIdFromDto, null);
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

        if (trip.getStops() == null) {
            trip.setStops(new ArrayList<>());
        }
        tripStopSyncService.syncStopsForTrip(trip);

        if (legFares != null && !legFares.isEmpty()) {
            double sum = tripPricingService.validateConsecutiveLegFaresAndSum(trip, legFares);
            tripRunService.ensureStops(trip);
            int lastIdx = tripRunService.lastStopIndex(trip);
            if (lastIdx > 1) {
                if (trip.getOriginDestinationPrice() == null || trip.getOriginDestinationPrice() <= 0) {
                    throw new MobiliException(
                            MobiliErrorCode.VALIDATION_ERROR,
                            "Indiquez le prix du trajet complet (départ → arrivée) : il peut différer de la somme des tronçons.");
                }
                trip.setPrice(trip.getOriginDestinationPrice());
            } else {
                trip.setPrice(sum);
                trip.setOriginDestinationPrice(null);
            }
        } else {
            trip.setOriginDestinationPrice(null);
        }

        Trip saved = tripRepository.save(trip);

        if (isNew) {
            analyticsEventService.record(
                    AnalyticsEventType.TRIP_PUBLISHED,
                    principal.getUser().getId(),
                    String.format("{\"tripId\":%d,\"partnerId\":%d}", saved.getId(), partner.getId()));
        }

        if (legFares != null) {
            if (legFares.isEmpty()) {
                tripPricingService.clearSegmentFaresForTrip(saved.getId());
            } else {
                tripPricingService.replaceConsecutiveLegFares(saved, legFares);
            }
        }

        return saved;
    }

    private void assertTripWriteAccess(Trip existing, UserPrincipal p, Partner currentPartner) {
        if (!existing.getPartner().getId().equals(currentPartner.getId())) {
            throw new MobiliException(MobiliErrorCode.ACCESS_DENIED, "Voyage d'un autre partenaire");
        }
        if (p.getStationId() != null) {
            if (existing.getStation() == null
                    || !existing.getStation().getId().equals(p.getStationId())) {
                throw new MobiliException(MobiliErrorCode.ACCESS_DENIED, "Ce voyage n'appartient pas à votre gare");
            }
        }
    }

    private void applyStationOnWrite(
            Trip trip, UserPrincipal principal, Partner partner, Long stationIdFromDto, Trip existing) {
        if (principal.getStationId() != null) {
            Station st = stationService.getStationForPartnerOrThrow(principal.getStationId(), partner.getId());
            trip.setStation(st);
            stationService.assertStationOperationalForTripUse(st);
            return;
        }
        if (stationIdFromDto != null) {
            Station st = stationService.getStationForPartnerOrThrow(stationIdFromDto, partner.getId());
            trip.setStation(st);
            stationService.assertStationOperationalForTripUse(st);
        } else if (existing != null) {
            trip.setStation(existing.getStation());
            if (trip.getStation() != null) {
                stationService.assertStationOperationalForTripUse(trip.getStation());
            }
        } else {
            trip.setStation(null);
        }
    }

    @Transactional
    public void delete(Long id, UserPrincipal principal) {
        Trip t = findById(id);
        assertTripWriteAccess(t, principal, partenaireService.getCurrentPartner());
        tripPricingService.clearSegmentFaresForTrip(id);
        tripRepository.delete(t);
    }

    /** Vérifie qu’un compte gare n’agit que sur les voyages de sa gare (console conducteur). Chauffeur / admin : pas de filtre ici. */
    @Transactional(readOnly = true)
    public void assertPartnerOrGareCanOperateDriverTrip(Long tripId, UserPrincipal principal) {
        Trip t = findById(tripId);
        if (principal.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority())
                || "ROLE_CHAUFFEUR".equals(a.getAuthority()))) {
            return;
        }
        Partner cp = partenaireService.getCurrentPartner();
        if (!t.getPartner().getId().equals(cp.getId())) {
            throw new MobiliException(MobiliErrorCode.ACCESS_DENIED, "Voyage d'un autre partenaire");
        }
        if (principal.getStationId() != null) {
            if (t.getStation() == null
                    || !t.getStation().getId().equals(principal.getStationId())) {
                throw new MobiliException(MobiliErrorCode.ACCESS_DENIED, "Hors périmètre de votre gare");
            }
        }
    }

    /**
     * Liste les arrêts d'un voyage. Pas en {@code readOnly} car {@link #findById}
     * peut persister à la volée les arrêts manquants (ancien voyage migré sans
     * la table trip_stops alimentée).
     */
    @Transactional
    public List<TripStopResponseDTO> listStops(Long tripId) {
        Trip t = findById(tripId);
        return t.getStops().stream()
                .map(s -> new TripStopResponseDTO(s.getStopIndex(), s.getCityLabel(), s.getPlannedDepartureAt()))
                .toList();
    }

    /**
     * Prévisualisation tarif segment : même chaîne que la réservation (arrêts + prorata), sans persistance.
     */
    @Transactional(readOnly = true)
    public TripPricePreviewResponse previewSegmentPrice(TripPricePreviewRequest req) {
        Trip draft = createTransientTripForPreview(req);
        tripRunService.validateSegment(draft, req.getBoardingStopIndex(), req.getAlightingStopIndex());
        tripRunService.ensureStops(draft);
        int last = tripRunService.lastStopIndex(draft);
        double perSeat;
        if (req.getLegFares() != null && !req.getLegFares().isEmpty()) {
            tripPricingService.validateConsecutiveLegFaresAndSum(draft, req.getLegFares());
            if (req.getBoardingStopIndex() == 0
                    && req.getAlightingStopIndex() == last
                    && req.getOriginDestinationPrice() != null) {
                perSeat = req.getOriginDestinationPrice();
            } else {
                perSeat = tripPricingService.sumLegFaresForPath(
                        req.getLegFares(), req.getBoardingStopIndex(), req.getAlightingStopIndex());
            }
        } else {
            perSeat = tripPricingService.resolvePricePerSeat(
                    draft, req.getBoardingStopIndex(), req.getAlightingStopIndex());
        }
        List<TripStopResponseDTO> stops = draft.getStops().stream()
                .map(s -> new TripStopResponseDTO(s.getStopIndex(), s.getCityLabel(), s.getPlannedDepartureAt()))
                .toList();
        return new TripPricePreviewResponse(perSeat, last, stops);
    }

    private static Trip createTransientTripForPreview(TripPricePreviewRequest req) {
        Trip t = new Trip();
        t.setDepartureCity(req.getDepartureCity());
        t.setArrivalCity(req.getArrivalCity());
        t.setMoreInfo(req.getMoreInfo() != null ? req.getMoreInfo() : "");
        t.setPrice(req.getPrice());
        LocalDateTime when = req.getDepartureDateTime() != null ? req.getDepartureDateTime() : LocalDateTime.now();
        t.setDepartureDateTime(when);
        t.setVehiculePlateNumber("—");
        t.setTotalSeats(1);
        t.setAvailableSeats(1);
        t.setVehicleType(VehicleType.MASSA_NORMAL);
        t.setStops(new ArrayList<>());
        t.setOriginDestinationPrice(req.getOriginDestinationPrice());
        return t;
    }
}