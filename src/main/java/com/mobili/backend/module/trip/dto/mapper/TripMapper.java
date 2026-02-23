package com.mobili.backend.module.trip.dto.mapper;

import com.mobili.backend.module.trip.dto.TripRequestDTO;
import com.mobili.backend.module.trip.dto.TripResponseDTO;
import com.mobili.backend.module.trip.entity.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TripMapper {

    // --- LECTURE (Ce que tu avais déjà) ---
    @Mapping(source = "route.departureCity.cityName", target = "departureCityName")
    @Mapping(source = "route.arrivalCity.cityName", target = "arrivalCityName")
    @Mapping(source = "vehicle.company.name", target = "companyName")
    @Mapping(source = "vehicle.company.logoUrl", target = "companyLogo")
    @Mapping(source = "vehicle.type", target = "vehicleType")
    TripResponseDTO toDto(Trip trip);

    // --- ÉCRITURE (À rajouter pour le TripWriteController) ---
    @Mapping(source = "routeId", target = "route.id")
    @Mapping(source = "vehicleId", target = "vehicle.id")
    Trip toEntity(TripRequestDTO dto);
}