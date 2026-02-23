package com.mobili.backend.module.trip.controller.trip;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.mobili.backend.module.trip.dto.TripRequestDTO;
import com.mobili.backend.module.trip.dto.TripResponseDTO;
import com.mobili.backend.module.trip.dto.mapper.TripMapper;
import com.mobili.backend.module.trip.entity.Trip;
import com.mobili.backend.module.trip.service.TripService;



@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripWriteController {

    private final TripService tripService;
    private final TripMapper tripMapper = Mappers.getMapper(TripMapper.class);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TripResponseDTO create(@Valid @RequestBody TripRequestDTO dto) {
        // Mapping DTO (Ids) -> Entity
        Trip entity = tripMapper.toEntity(dto);
        // Sauvegarde via le service
        Trip savedTrip = tripService.save(entity);
        // Mapping Entity -> ResponseDTO (Noms complets)
        return tripMapper.toDto(savedTrip);
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