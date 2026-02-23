package com.mobili.backend.module.trip.controller.route;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.web.bind.annotation.*;

import com.mobili.backend.module.trip.dto.RouteDTO;
import com.mobili.backend.module.trip.dto.mapper.RouteMapper;
import com.mobili.backend.module.trip.service.RouteService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/routes")
@RequiredArgsConstructor
public class RouteReadController {

    private final RouteService routeService;
    private final RouteMapper routeMapper = Mappers.getMapper(RouteMapper.class);

    @GetMapping
    public List<RouteDTO> getAll() {
        return routeService.findAll().stream()
                .map(routeMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public RouteDTO getById(@PathVariable Long id) {
        return routeMapper.toDto(routeService.findById(id));
    }
}