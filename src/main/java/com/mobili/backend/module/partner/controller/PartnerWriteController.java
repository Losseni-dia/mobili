package com.mobili.backend.module.partner.controller;

import com.mobili.backend.module.partner.dto.PartnerProfileDTO;
import com.mobili.backend.module.partner.dto.PartnerRegisterDTO;
import com.mobili.backend.module.partner.dto.mapper.PartnerMapper;
import com.mobili.backend.module.partner.entity.Partner;
import com.mobili.backend.module.partner.service.PartnerService;
import com.mobili.backend.shared.sharedService.UploadService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/partners")
@RequiredArgsConstructor
public class PartnerWriteController {

    private final PartnerService partenaireService;
    private final PartnerMapper partenaireMapper;
    private final UploadService uploadService;

    // INSCRIPTION avec LOGO
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public PartnerProfileDTO register(
            @RequestPart("partner") @Valid PartnerRegisterDTO dto,
            @RequestPart(value = "logo", required = false) MultipartFile logoFile) {

        Partner entity = partenaireMapper.toEntity(dto);

        if (logoFile != null && !logoFile.isEmpty()) {
            String path = uploadService.saveImage(logoFile, "logos");
            entity.setLogoUrl(path);
        }

        return partenaireMapper.toProfileDto(partenaireService.save(entity));
    }

    // MISE À JOUR DU PROFIL
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PartnerProfileDTO update(
            @PathVariable Long id,
            @RequestPart("partenaire") @Valid PartnerProfileDTO dto,
            @RequestPart(value = "logo", required = false) MultipartFile logoFile) {

        dto.setId(id);
        Partner entity = partenaireMapper.toEntity(dto);

        if (logoFile != null && !logoFile.isEmpty()) {
            String path = uploadService.saveImage(logoFile, "logos");
            entity.setLogoUrl(path);
        }

        return partenaireMapper.toProfileDto(partenaireService.save(entity));
    }

    @PatchMapping("/{id}/toggle")
    public void toggleStatus(@PathVariable Long id) {
        partenaireService.toggleStatus(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        partenaireService.delete(id);
    }
}
