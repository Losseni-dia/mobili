package com.mobili.backend.module.trip.dto.mapper;

import com.mobili.backend.module.trip.dto.TripResponseDTO;
import com.mobili.backend.module.trip.entity.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TripMapper {

    @Mapping(source = "route.departureCity.cityName", target = "departureCityName")
    @Mapping(source = "route.arrivalCity.cityName", target = "arrivalCityName")

    // On passe par le véhicule pour atteindre la compagnie
    @Mapping(source = "vehicle.company.name", target = "companyName")
    @Mapping(source = "vehicle.company.logoUrl", target = "companyLogo")

    // On peut aussi mapper le type de véhicule si besoin
    @Mapping(source = "vehicle.type", target = "vehicleType")

    TripResponseDTO toDto(Trip trip);
}