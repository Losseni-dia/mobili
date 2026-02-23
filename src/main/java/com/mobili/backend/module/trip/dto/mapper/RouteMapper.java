package com.mobili.backend.module.trip.dto.mapper;

import com.mobili.backend.module.trip.dto.RouteDTO;
import com.mobili.backend.module.trip.entity.Route;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RouteMapper {

    RouteMapper INSTANCE = Mappers.getMapper(RouteMapper.class);

    @Mapping(source = "departureCity.id", target = "departureCityId")
    @Mapping(source = "arrivalCity.id", target = "arrivalCityId")
    @Mapping(source = "departureCity.cityName", target = "departureCityName")
    @Mapping(source = "arrivalCity.cityName", target = "arrivalCityName")
    RouteDTO toDto(Route route);

    @Mapping(source = "departureCityId", target = "departureCity.id")
    @Mapping(source = "arrivalCityId", target = "arrivalCity.id")
    Route toEntity(RouteDTO dto);
}