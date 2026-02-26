package com.mobili.backend.module.transport.controller.company;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
@PostMapping(consumes = { "multipart/form-data" })
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyDTO create(
            @RequestPart("company") @Valid CompanyDTO dto,
            @RequestPart(value = "logo", required = false) MultipartFile logoFile) {
        
        Company entity = companyMapper.toEntity(dto);
        // On passe le fichier au service pour traitement
        return companyMapper.toDto(companyService.saveWithLogo(entity, logoFile));
    }

    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public CompanyDTO update(
            @PathVariable Long id,
            @RequestPart("company") @Valid CompanyDTO dto,
            @RequestPart(value = "logo", required = false) MultipartFile logoFile) {

        dto.setId(id);
        Company entity = companyMapper.toEntity(dto);

        // On utilise la même logique que le create pour traiter le fichier s'il existe
        return companyMapper.toDto(companyService.saveWithLogo(entity, logoFile));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        companyService.delete(id);
    }
}