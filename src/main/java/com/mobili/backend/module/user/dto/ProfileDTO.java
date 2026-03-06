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
    private List<String> roles;
    private Long partnerId; // 💡 AJOUTE CETTE LIGNE
    private Integer totalBookingsCount;
}