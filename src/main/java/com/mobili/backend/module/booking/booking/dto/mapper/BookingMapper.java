package com.mobili.backend.module.booking.booking.dto.mapper;

import com.mobili.backend.module.booking.booking.dto.BookingResponseDTO;
import com.mobili.backend.module.booking.booking.entity.Booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(source = "trip.departureCity", target = "departureCity")
    @Mapping(source = "trip.arrivalCity", target = "arrivalCity")
    @Mapping(source = "trip.departureDateTime", target = "departureDateTime")
    BookingResponseDTO toDto(Booking booking);
}