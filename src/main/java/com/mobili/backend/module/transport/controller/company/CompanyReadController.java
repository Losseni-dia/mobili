package com.mobili.backend.module.transport.controller.company;

import com.mobili.backend.module.transport.dto.CompanyDTO;
import com.mobili.backend.module.transport.dto.mapper.CompanyMapper;
import com.mobili.backend.module.transport.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/companies")
@RequiredArgsConstructor
public class CompanyReadController {

    private final CompanyService companyService;
    private final CompanyMapper companyMapper = Mappers.getMapper(CompanyMapper.class);

    @GetMapping
    public List<CompanyDTO> getAll() {
        return companyService.findAll().stream()
                .map(companyMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CompanyDTO getById(@PathVariable Long id) {
        return companyMapper.toDto(companyService.findById(id));
    }
}