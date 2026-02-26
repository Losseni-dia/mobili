package com.mobili.backend.module.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mobili.backend.module.booking.entity.Booking;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Retrouver toutes les réservations d'un voyage spécifique
    List<Booking> findByTripId(Long tripId);

    // Retrouver les réservations par numéro de téléphone (Historique utilisateur)
    List<Booking> findByPassengerPhoneOrderByCreatedAtDesc(String passengerPhone);

    // Compter le nombre de réservations pour un voyage (Utile pour double
    // vérification)
    long countByTripId(Long tripId);
}