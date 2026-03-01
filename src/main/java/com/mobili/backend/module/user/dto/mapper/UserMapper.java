package com.mobili.backend.module.user.dto.mapper;

import com.mobili.backend.module.user.dto.ProfileDTO;
import com.mobili.backend.module.user.dto.RegisterDTO;
import com.mobili.backend.module.user.entity.User;
import com.mobili.backend.module.user.role.Role;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // ÉCRITURE
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    User toEntity(RegisterDTO dto);

    // LECTURE
    // On s'assure que MapStruct mappe bien les champs même avec le @Builder du DTO
    @Mapping(source = "roles", target = "roles", qualifiedByName = "mapRoles")
    @Mapping(source = "firstname", target = "firstname") // Optionnel si noms identiques, mais plus sûr
    @Mapping(source = "lastname", target = "lastname")
    @Mapping(source = "avatarUrl", target = "avatarUrl")
    ProfileDTO toProfileDto(User user);

    @Named("mapRoles")
    default Set<String> mapRoles(Set<Role> roles) {
        if (roles == null)
            return null;
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }
}