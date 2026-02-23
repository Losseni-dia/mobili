package com.mobili.backend.module.trip.controller.city;

import com.mobili.backend.module.trip.dto.CityDTO;
import com.mobili.backend.module.trip.dto.mapper.CityMapper;
import com.mobili.backend.module.trip.service.CityService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/cities")
@RequiredArgsConstructor
public class CityReadController {

    private final CityService cityService;
    private final CityMapper cityMapper = Mappers.getMapper(CityMapper.class);

    @GetMapping
    public List<CityDTO> getAll() {
        return cityService.findAll().stream()
                .map(cityMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CityDTO getById(@PathVariable Long id) {
        return cityMapper.toDto(cityService.findById(id));
    }
}