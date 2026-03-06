package com.mobili.backend.module.admin.dto;

public record PartnerAdminResponse(
                Long id,
                String name,
                String email,
                String phone,
                String businessNumber,
                boolean enabled,
                String ownerName, 
                String logoUrl) {
}
