package com.mobili.backend.module.transport.controller.company;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.mobili.backend.module.transport.dto.CompanyDTO;
import com.mobili.backend.module.transport.dto.mapper.CompanyMapper;
import com.mobili.backend.module.transport.entity.Company;
import com.mobili.backend.module.transport.service.CompanyService;



@RestController
@RequestMapping("/v1/companies")
@RequiredArgsConstructor
public class CompanyWriteController {

    private final CompanyService companyService;
    private final CompanyMapper companyMapper = Mappers.getMapper(CompanyMapper.class);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyDTO create(@Valid @RequestBody CompanyDTO dto) {
        Company entity = companyMapper.toEntity(dto);
        return companyMapper.toDto(companyService.save(entity));
    }

    @PutMapping("/{id}")
    public CompanyDTO update(@PathVariable Long id, @Valid @RequestBody CompanyDTO dto) {
        dto.setId(id);
        Company entity = companyMapper.toEntity(dto);
        return companyMapper.toDto(companyService.save(entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        companyService.delete(id);
    }
}