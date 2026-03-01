package com.mobili.backend.module.partner.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.mobili.backend.module.partner.dto.PartnerProfileDTO;
import com.mobili.backend.module.partner.dto.mapper.PartnerMapper;
import com.mobili.backend.module.partner.service.PartnerService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/partners")
@RequiredArgsConstructor
public class PartenerReadController {

    private final PartnerService partenaireService;
    private final PartnerMapper partenaireMapper;

    @GetMapping
    public List<PartnerProfileDTO> getAll() {
        return partenaireService.findAll().stream()
                .map(partenaireMapper::toProfileDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PartnerProfileDTO getById(@PathVariable Long id) {
        return partenaireMapper.toProfileDto(partenaireService.findById(id));
    }
}