package com.mobili.backend.module.trip.controller.city;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.mobili.backend.module.trip.dto.CityDTO;
import com.mobili.backend.module.trip.dto.mapper.CityMapper;
import com.mobili.backend.module.trip.entity.City;
import com.mobili.backend.module.trip.service.CityService;


@RestController
@RequestMapping("/v1/cities")
@RequiredArgsConstructor
public class CityWriteController {

    private final CityService cityService;
    private final CityMapper cityMapper = Mappers.getMapper(CityMapper.class);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CityDTO create(@Valid @RequestBody CityDTO dto) {
        City entity = cityMapper.toEntity(dto);
        return cityMapper.toDto(cityService.save(entity));
    }

    @PutMapping("/{id}")
    public CityDTO update(@PathVariable Long id, @Valid @RequestBody CityDTO dto) {
        dto.setId(id);
        City entity = cityMapper.toEntity(dto);
        return cityMapper.toDto(cityService.save(entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        cityService.delete(id);
    }
}