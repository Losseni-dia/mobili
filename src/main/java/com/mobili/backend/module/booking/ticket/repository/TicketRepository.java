package com.mobili.backend.module.booking.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mobili.backend.module.booking.ticket.entity.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByPassengerId(Long userId);
    
    Optional<Ticket> findByTicketNumber(String ticketNumber);

    @Query("SELECT t.seatNumber FROM Ticket t WHERE t.trip.id = :tripId AND t.status != 'ANNULÉ'")
    List<String> findOccupiedSeatNumbersByTripId(@Param("tripId") Long tripId);
}