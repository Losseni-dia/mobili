package com.mobili.backend.module.trip.dto.mapper;

import org.mapstruct.Mapper;

import com.mobili.backend.module.trip.dto.CityDTO;
import com.mobili.backend.module.trip.entity.City;

@Mapper
public interface CityMapper {
    CityDTO toDto(City city);

    City toEntity(CityDTO dto);
}