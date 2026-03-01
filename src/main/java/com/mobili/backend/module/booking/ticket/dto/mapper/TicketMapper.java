package com.mobili.backend.module.booking.ticket.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mobili.backend.module.booking.ticket.dto.TicketResponseDTO;
import com.mobili.backend.module.booking.ticket.entity.Ticket;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(target = "passengerFullName", expression = "java(ticket.getPassenger().getFirstname() + ' ' + ticket.getPassenger().getLastname())")
    @Mapping(source = "ticketNumber", target = "qrCodeData") // Le contenu du QR Code
    @Mapping(source = "trip.departureCity", target = "departureCity")
    @Mapping(source = "trip.arrivalCity", target = "arrivalCity")
    @Mapping(source = "trip.departureDateTime", target = "departureDateTime")
    @Mapping(source = "trip.vehiculePlateNumber", target = "vehiculePlateNumber")
    @Mapping(source = "amountPaid", target = "price")
    TicketResponseDTO toDto(Ticket ticket);
}
