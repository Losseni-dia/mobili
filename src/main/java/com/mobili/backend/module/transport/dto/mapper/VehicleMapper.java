package com.mobili.backend.module.transport.dto.mapper;

import com.mobili.backend.module.transport.dto.VehicleDTO;
import com.mobili.backend.module.transport.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface VehicleMapper {

    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "company.name", target = "companyName")
    VehicleDTO toDto(Vehicle vehicle);

    @Mapping(source = "companyId", target = "company.id")
    Vehicle toEntity(VehicleDTO dto);
}