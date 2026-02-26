package com.mobili.backend.module.transport.service;

import com.mobili.backend.module.transport.entity.Vehicle;
import com.mobili.backend.module.transport.repository.VehicleRepository;
import com.mobili.backend.shared.mobiliError.exception.MobiliErrorCode;
import com.mobili.backend.shared.mobiliError.exception.MobiliException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    // --- READ ---
    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    public Vehicle findById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new MobiliException(
                        MobiliErrorCode.RESOURCE_NOT_FOUND,
                        "Véhicule introuvable (ID: " + id + ")"));
    }

    public List<Vehicle> findByCompany(Long companyId) {
        return vehicleRepository.findByCompanyId(companyId);
    }

    // --- WRITE ---
    @Transactional
    public Vehicle save(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public void delete(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new MobiliException(
                    MobiliErrorCode.RESOURCE_NOT_FOUND,
                    "Impossible de supprimer : Véhicule introuvable");
        }
        vehicleRepository.deleteById(id);
    }
}