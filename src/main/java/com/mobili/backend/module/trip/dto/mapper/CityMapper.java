package com.mobili.backend.module.trip.dto.mapper;

import com.mobili.backend.module.trip.dto.CityDTO;
import com.mobili.backend.module.trip.entity.City;

import org.mapstruct.Mapper;

@Mapper
public interface CityMapper {
    CityDTO toDto(City city);

    City toEntity(CityDTO dto);
}