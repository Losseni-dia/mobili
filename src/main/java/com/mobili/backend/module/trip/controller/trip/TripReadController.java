package com.mobili.backend.module.trip.controller.trip;

import com.mobili.backend.module.trip.dto.TripResponseDTO;
import com.mobili.backend.module.trip.dto.mapper.TripMapper;
import com.mobili.backend.module.trip.entity.Trip;
import com.mobili.backend.module.trip.service.TripService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripReadController {

    private final TripService tripService;
    private final TripMapper tripMapper = Mappers.getMapper(TripMapper.class);

    @GetMapping
    public List<TripResponseDTO> getAll() {
        return tripService.findAll().stream()
                .map(tripMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TripResponseDTO getById(@PathVariable Long id) {
        return tripMapper.toDto(tripService.findById(id));
    }

    // Le point de recherche pour ton formulaire Angular
    @GetMapping("/search")
    public List<TripResponseDTO> search(
            @RequestParam String departure,
            @RequestParam String arrival,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<Trip> results = tripService.searchTrips(departure, arrival, date);

        return results.stream()
                .map(tripMapper::toDto)
                .collect(Collectors.toList());
    }
}