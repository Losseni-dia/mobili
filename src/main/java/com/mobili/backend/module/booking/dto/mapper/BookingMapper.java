package com.mobili.backend.module.booking.entity;

package com.mobili.backend.module.booking.dto.mapper;

import com.mobili.backend.module.booking.dto.BookingRequestDTO;
import com.mobili.backend.module.booking.dto.BookingResponseDTO;
import com.mobili.backend.module.booking.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookingMapper {

    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    // --- LECTURE (Ticket) ---
    @Mapping(source = "trip.route.departureCity.cityName", target = "departureCity")
    @Mapping(source = "trip.route.arrivalCity.cityName", target = "arrivalCity")
    @Mapping(source = "trip.departureDateTime", target = "departureDateTime")
    @Mapping(source = "trip.vehicle.company.name", target = "companyName")
    BookingResponseDTO toDto(Booking entity);

    // --- ÉCRITURE (Réservation) ---
    @Mapping(source = "tripId", target = "trip.id")
    Booking toEntity(BookingRequestDTO dto);
}