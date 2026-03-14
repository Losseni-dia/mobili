package com.mobili.backend.module.partner.dto;

import lombok.Data;

@Data
public class PartnerProfileDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String logoUrl;
    private String businessNumber;
    private boolean enabled;
}