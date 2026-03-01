package com.mobili.backend.module.booking.booking.entity;

import lombok.Getter;

@Getter
public enum BookingStatus {
    PENDING("En attente"),
    CONFIRMED("Confirmé"),
    CANCELLED("Annulé"),
    COMPLETED("Terminé");

    private final String label;

    BookingStatus(String label) {
        this.label = label;
    }
}