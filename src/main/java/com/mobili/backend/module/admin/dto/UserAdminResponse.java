package com.mobili.backend.module.admin.dto;

import java.util.List;

public record UserAdminResponse(
        Long id,
        String firstname,
        String lastname,
        String email,
        List<String> roles,
        boolean enabled,
        String partnerName // Pour savoir si cet user est aussi un partenaire
) {
}