package com.mobili.backend.module.user.dto.mapper;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.mobili.backend.module.admin.dto.UserAdminResponse;
import com.mobili.backend.module.user.dto.ProfileDTO;
import com.mobili.backend.module.user.dto.RegisterDTO;
import com.mobili.backend.module.user.dto.UpdateUserDTO;
import com.mobili.backend.module.user.entity.User;
import com.mobili.backend.module.user.role.Role;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // LECTURE
    @Mapping(target = "roles", qualifiedByName = "mapRolesToStrings")
    @Mapping(target = "partnerId", expression = "java(user.getPartner() != null ? user.getPartner().getId() : null)")
    @Mapping(target = "totalBookingsCount", ignore = true) // 💡 Évite les erreurs si vide
    ProfileDTO toProfileDto(User user);

    @Named("mapRolesToStrings")
    default List<String> mapRolesToStrings(Set<Role> roles) {
        if (roles == null)
            return List.of();

        return roles.stream()
                .map(role -> role.getName().name()) // On extrait le nom de l'Enum
                .toList(); // 👈 On transforme en List pour matcher le DTO
    }

    // ÉCRITURE (Register)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    User toEntity(RegisterDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    User toEntity(UpdateUserDTO dto);

    @Mapping(target = "roles", qualifiedByName = "mapRolesToStrings")
    @Mapping(target = "partnerName", expression = "java(user.getPartner() != null ? user.getPartner().getName() : null)")
    UserAdminResponse toAdminDto(User user);
}