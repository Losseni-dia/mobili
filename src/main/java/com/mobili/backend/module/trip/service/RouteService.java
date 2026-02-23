package com.mobili.backend.module.trip.service;

import com.mobili.backend.module.trip.entity.Route;
import com.mobili.backend.module.trip.repository.RouteRepository;
import com.mobili.backend.shared.mobiliError.exception.MobiliErrorCode;
import com.mobili.backend.shared.mobiliError.exception.MobiliException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;

    public List<Route> findAll() {
        return routeRepository.findAll();
    }

    public Route findById(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new MobiliException(
                        MobiliErrorCode.RESOURCE_NOT_FOUND,
                        "Route introuvable (ID: " + id + ")"));
    }

    @Transactional
    public Route save(Route route) {
        return routeRepository.save(route);
    }

    @Transactional
    public void delete(Long id) {
        if (!routeRepository.existsById(id)) {
            throw new MobiliException(
                    MobiliErrorCode.RESOURCE_NOT_FOUND,
                    "Impossible de supprimer : Route introuvable");
        }
        routeRepository.deleteById(id);
    }
}