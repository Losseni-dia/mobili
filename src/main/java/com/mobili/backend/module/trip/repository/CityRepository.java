package com.mobili.backend.module.trip.repository;

import com.mobili.backend.module.trip.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    // Pour éviter de créer deux fois la même ville par erreur
    Optional<City> findByCityNameIgnoreCase(String cityName);

    // Pour filtrer les villes par pays (Utile si tu t'étends à plusieurs pays)
    List<City> findByCountryOrderByCityNameAsc(String country);
}