package com.mobili.backend.module.user.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class ProfileDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String login;
    private String email;
    private String avatarUrl;
    private boolean enabled;

    // Noms des rôles pour gérer les accès dans Angular
    private Set<String> roles;
    private Integer totalBookingsCount;
}