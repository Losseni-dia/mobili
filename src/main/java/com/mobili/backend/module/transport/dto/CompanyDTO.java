package com.mobili.backend.module.transport.dto;

import lombok.Data;

@Data
public class CompanyDTO {
    private Long id;
    private String name;
    private String logoUrl;
    private String country;
}