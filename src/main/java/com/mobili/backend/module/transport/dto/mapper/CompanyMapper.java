package com.mobili.backend.module.transport.dto.mapper;

import org.mapstruct.Mapper;

import com.mobili.backend.module.transport.dto.CompanyDTO;
import com.mobili.backend.module.transport.entity.Company;

@Mapper
public interface CompanyMapper {
    CompanyDTO toDto(Company company);

    Company toEntity(CompanyDTO dto);
}