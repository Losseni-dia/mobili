package com.mobili.backend.module.trip.service;

import com.mobili.backend.module.trip.entity.City;
import com.mobili.backend.module.trip.repository.CityRepository;
import com.mobili.backend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    public List<City> findAll() {
        return cityRepository.findAll();
    }

    public City findById(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ville introuvable (ID: " + id + ")"));
    }

    @Transactional
    public City save(City city) {
        return cityRepository.save(city);
    }

    @Transactional
    public void delete(Long id) {
        if (!cityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Impossible de supprimer : Ville introuvable");
        }
        cityRepository.deleteById(id);
    }
}