package com.mobili.backend.module.transport.controller.vehicule;

import com.mobili.backend.module.transport.dto.VehicleDTO;
import com.mobili.backend.module.transport.dto.mapper.VehicleMapper;
import com.mobili.backend.module.transport.entity.Vehicle;
import com.mobili.backend.module.transport.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vehicles") // Plus de /api ici
@RequiredArgsConstructor
public class VehicleWriteController {

    private final VehicleService vehicleService;

    // Initialisation manuelle du mapper sans passer par l'injection Spring
    private final VehicleMapper vehicleMapper = Mappers.getMapper(VehicleMapper.class);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleDTO create(@Valid @RequestBody VehicleDTO dto) {
        // Utilisation de l'attribut local vehicleMapper
        Vehicle entity = vehicleMapper.toEntity(dto);
        Vehicle savedEntity = vehicleService.save(entity);
        return vehicleMapper.toDto(savedEntity);
    }

    @PutMapping("/{id}")
    public VehicleDTO update(@PathVariable Long id, @Valid @RequestBody VehicleDTO dto) {
        dto.setId(id);
        // Utilisation de l'attribut local vehicleMapper
        Vehicle entity = vehicleMapper.toEntity(dto);
        Vehicle updatedEntity = vehicleService.save(entity);
        return vehicleMapper.toDto(updatedEntity);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        vehicleService.delete(id);
    }
}