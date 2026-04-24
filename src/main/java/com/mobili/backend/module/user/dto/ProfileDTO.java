package com.mobili.backend.module.user.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String login;
    private String email;
    private String avatarUrl;
    private boolean enabled;
    private Double balance;
    private List<String> roles;
    private Long partnerId;
    /** Compte gare (responsable d’une gare) */
    private Long stationId;
    private String stationName;
    /**
     * Rôle GARE : la gare est validée (booléen {@code Station.validated}) et active, donc
     * trajets, scanner, accès compagnie autorisés. {@code null} si pas gare.
     */
    private Boolean gareOperationsEnabled;
    private Integer totalBookingsCount;
}