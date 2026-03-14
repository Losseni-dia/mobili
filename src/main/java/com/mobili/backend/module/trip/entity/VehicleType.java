package com.mobili.backend.module.trip.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum VehicleType {
    BUS_CLIMATISE("Bus Climatisé"),
    BUS_CLASSIQUE("Bus Classique"),
    CAR_CLIMATISE("Car Climatisé"),
    CAR_CLASSIQUE("Car Classique"),
    MINIBUS("Minibus"),
    VAN("Van");

    private final String label;

    VehicleType(String label) {
        this.label = label;
    }

    // On garde getLabel pour le code Java
    public String getLabel() {
        return label;
    }

    // 💡 On force Jackson à utiliser le label pour le JSON
    @Override
    @JsonValue
    public String toString() {
        return label;
    }
}