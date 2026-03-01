package com.mobili.backend.module.booking.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mobili.backend.module.booking.ticket.entity.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByPassengerId(Long userId);
    
    Optional<Ticket> findByTicketNumber(String ticketNumber);
}