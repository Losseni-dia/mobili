package com.mobili.backend.module.trip.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mobili.backend.module.trip.dto.TripRequestDTO;
import com.mobili.backend.module.trip.dto.TripResponseDTO;
import com.mobili.backend.module.trip.dto.mapper.TripMapper;
import com.mobili.backend.module.trip.entity.Trip;
import com.mobili.backend.module.trip.service.TripService;
import com.mobili.backend.shared.sharedService.UploadService;

@RestController
@RequestMapping("/v1/trips")
@RequiredArgsConstructor
public class TripWriteController {

    private final TripService tripService;
    private final UploadService uploadService;
    private final TripMapper tripMapper = Mappers.getMapper(TripMapper.class);

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('PARTNER', 'ADMIN')")
    public TripResponseDTO create(
            @RequestPart("trip") @Valid TripRequestDTO dto,
            @RequestPart(value = "vehicleImage", required = false) MultipartFile file) {

        Trip entity = tripMapper.toEntity(dto);

        if (file != null && !file.isEmpty()) {
            // On range les photos de bus dans un dossier "vehicles"
            String path = uploadService.saveImage(file, "vehicles");
            entity.setVehicleImageUrl(path);
        }

        return tripMapper.toDto(tripService.save(entity));
    }

    @PutMapping("/{id}")
    public TripResponseDTO update(@PathVariable Long id, @Valid @RequestBody TripRequestDTO dto) {
        dto.setId(id);
        Trip entity = tripMapper.toEntity(dto);
        Trip updatedTrip = tripService.save(entity);
        return tripMapper.toDto(updatedTrip);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        tripService.delete(id);
    }
}