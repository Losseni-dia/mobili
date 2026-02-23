package com.mobili.backend.module.transport.dto.mapper;

import com.mobili.backend.module.transport.dto.CompanyDTO;
import com.mobili.backend.module.transport.entity.Company;
import org.mapstruct.Mapper;

@Mapper
public interface CompanyMapper {
    CompanyDTO toDto(Company company);

    Company toEntity(CompanyDTO dto);
}