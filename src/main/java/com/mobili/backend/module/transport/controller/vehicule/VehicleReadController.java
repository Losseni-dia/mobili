package com.mobili.backend.module.transport.controller.vehicule;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.web.bind.annotation.*;

import com.mobili.backend.module.transport.dto.VehicleDTO;
import com.mobili.backend.module.transport.dto.mapper.VehicleMapper;
import com.mobili.backend.module.transport.service.VehicleService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/vehicles")
@RequiredArgsConstructor
public class VehicleReadController {

    private final VehicleService vehicleService;

    // Instance locale du mapper
    private final VehicleMapper vehicleMapper = Mappers.getMapper(VehicleMapper.class);

    @GetMapping
    public List<VehicleDTO> getAll() {
        return vehicleService.findAll().stream()
                .map(vehicleMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public VehicleDTO getById(@PathVariable Long id) {
        return vehicleMapper.toDto(vehicleService.findById(id));
    }

    @GetMapping("/company/{companyId}")
    public List<VehicleDTO> getByCompany(@PathVariable Long companyId) {
        return vehicleService.findByCompany(companyId).stream()
                .map(vehicleMapper::toDto)
                .collect(Collectors.toList());
    }
}