package com.mobili.backend.module.transport.dto.mapper;

import com.mobili.backend.module.transport.dto.CityDTO;
import com.mobili.backend.module.transport.entity.City;
import org.mapstruct.Mapper;

@Mapper
public interface CityMapper {
    CityDTO toDto(City city);

    City toEntity(CityDTO dto);
}