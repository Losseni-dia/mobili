package com.mobili.backend.module.trip.controller.route;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.mobili.backend.module.trip.dto.RouteDTO;
import com.mobili.backend.module.trip.dto.mapper.RouteMapper;
import com.mobili.backend.module.trip.entity.Route;
import com.mobili.backend.module.trip.service.RouteService;



@RestController
@RequestMapping("/v1/routes")
@RequiredArgsConstructor
public class RouteWriteController {

    private final RouteService routeService;
    private final RouteMapper routeMapper = Mappers.getMapper(RouteMapper.class);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RouteDTO create(@Valid @RequestBody RouteDTO dto) {
        Route entity = routeMapper.toEntity(dto);
        return routeMapper.toDto(routeService.save(entity));
    }

    @PutMapping("/{id}")
    public RouteDTO update(@PathVariable Long id, @Valid @RequestBody RouteDTO dto) {
        dto.setId(id);
        Route entity = routeMapper.toEntity(dto);
        return routeMapper.toDto(routeService.save(entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        routeService.delete(id);
    }
}