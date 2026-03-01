package com.mobili.backend.module.trip.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum VehicleType {
    @JsonProperty("BUS_CLIMATISE")
    BUS_CLIMATISE,

    @JsonProperty("BUS_CLASSIQUE")
    BUS_CLASSIQUE,

    @JsonProperty("CAR_CLIMATISE")
    CAR_CLIMATISE,

    @JsonProperty("CAR_CLASSIQUE")
    CAR_CLASSIQUE,

    @JsonProperty("MINIBUS")
    MINIBUS,

    @JsonProperty("VAN")
    VAN
}