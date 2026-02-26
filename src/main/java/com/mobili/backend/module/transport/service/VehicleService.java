package com.mobili.backend.module.transport.service;

import com.mobili.backend.module.transport.entity.Company;
import com.mobili.backend.module.transport.entity.Vehicle;
import com.mobili.backend.module.transport.repository.CompanyRepository;
import com.mobili.backend.module.transport.repository.VehicleRepository;
import com.mobili.backend.shared.mobiliError.exception.MobiliErrorCode;
import com.mobili.backend.shared.mobiliError.exception.MobiliException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CompanyRepository companyRepository;

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
public Vehicle saveWithImage(Vehicle vehicle, Long companyId, MultipartFile imageFile) {
    // 1. Lier la compagnie (Obligatoire pour un véhicule)
    Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new RuntimeException("Compagnie introuvable"));
    vehicle.setCompany(company);

    // 2. Gestion de l'Update : conserver l'ancienne image si pas de nouvelle fournie
    if (vehicle.getId() != null && (imageFile == null || imageFile.isEmpty())) {
        vehicleRepository.findById(vehicle.getId()).ifPresent(existing -> {
            vehicle.setImageUrl(existing.getImageUrl());
        });
    } 
    // 3. Gestion de l'Upload
    else if (imageFile != null && !imageFile.isEmpty()) {
        try {
            String subFolder = "vehicles/";
            Path uploadPath = Paths.get(System.getProperty("user.dir"), ".uploads", subFolder);
            
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Enregistrement de l'URL web (sans le point)
            vehicle.setImageUrl("/uploads/" + subFolder + fileName);
            
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde de l'image véhicule", e);
        }
    }
    
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