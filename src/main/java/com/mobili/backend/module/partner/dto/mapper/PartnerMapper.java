package com.mobili.backend.module.partner.dto.mapper;

import org.mapstruct.Mapper;

import com.mobili.backend.module.partner.dto.PartnerProfileDTO;
import com.mobili.backend.module.partner.dto.PartnerRegisterDTO;
import com.mobili.backend.module.partner.entity.Partner;

@Mapper(componentModel = "spring")
public interface PartnerMapper {

    // Pour l'inscription (Register -> Entity)
    Partner toEntity(PartnerRegisterDTO dto);

    // Pour la mise à jour (Profile -> Entity)
    Partner toEntity(PartnerProfileDTO dto);

    // Pour l'affichage (Entity -> Profile)
    PartnerProfileDTO toProfileDto(Partner partner);
}