package com.mobili.backend.module.trip.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.mobili.backend.module.trip.dto.TripRequestDTO;
import com.mobili.backend.module.trip.dto.TripResponseDTO;
import com.mobili.backend.module.trip.entity.Trip;

@Mapper(componentModel = "spring")
public interface TripMapper {

    // --- ÉCRITURE (Request -> Entity) ---
    @Mapping(source = "partnerId", target = "partner.id")
    // MapStruct fera le lien automatique pour boardingPoint et moreInfo
    // car les noms sont maintenant identiques dans le DTO et l'Entité.
    Trip toEntity(TripRequestDTO dto);

    // --- LECTURE (Entity -> ResponseDTO) ---
    // Vérifie bien que dans ton entité Partenaire, le champ s'appelle 'name'
    // (ou change 'partner.name' par 'partner.nom' si nécessaire)
    @Mapping(source = "partner.name", target = "partnerName")
    TripResponseDTO toDto(Trip trip);
}