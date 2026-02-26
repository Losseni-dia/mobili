package com.mobili.backend.module.transport.controller.vehicule;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mobili.backend.module.transport.dto.VehicleDTO;
import com.mobili.backend.module.transport.dto.mapper.VehicleMapper;
import com.mobili.backend.module.transport.entity.Vehicle;
import com.mobili.backend.module.transport.service.VehicleService;


@RestController
@RequestMapping("/v1/vehicles") // Plus de /api ici
@RequiredArgsConstructor
public class VehicleWriteController {

    private final VehicleService vehicleService;

    // Initialisation manuelle du mapper sans passer par l'injection Spring
    private final VehicleMapper vehicleMapper = Mappers.getMapper(VehicleMapper.class);

 @PostMapping(consumes = { "multipart/form-data" })
@ResponseStatus(HttpStatus.CREATED)
public VehicleDTO create(
        @RequestPart("vehicle") @Valid VehicleDTO dto,
        @RequestPart(value = "image", required = false) MultipartFile imageFile) {
    
    Vehicle entity = vehicleMapper.toEntity(dto);
    // On appelle une méthode du service qui gère l'image et l'ID compagnie
    Vehicle savedEntity = vehicleService.saveWithImage(entity, dto.getCompanyId(), imageFile);
    return vehicleMapper.toDto(savedEntity);
}

@PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
public VehicleDTO update(
        @PathVariable Long id,
        @RequestPart("vehicle") @Valid VehicleDTO dto,
        @RequestPart(value = "image", required = false) MultipartFile imageFile) {

    dto.setId(id);
    Vehicle entity = vehicleMapper.toEntity(dto);

    // On passe l'ID de la compagnie stocké dans le DTO pour maintenir le lien
    Vehicle updatedEntity = vehicleService.saveWithImage(entity, dto.getCompanyId(), imageFile);

    return vehicleMapper.toDto(updatedEntity);
}

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        vehicleService.delete(id);
    }
}