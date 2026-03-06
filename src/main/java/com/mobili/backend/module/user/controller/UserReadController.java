package com.mobili.backend.module.user.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mobili.backend.infrastructure.security.authentication.UserPrincipal;
import com.mobili.backend.module.user.dto.ProfileDTO;
import com.mobili.backend.module.user.dto.mapper.UserMapper;
import com.mobili.backend.module.user.entity.User;
import com.mobili.backend.module.user.repository.UserRepository;
import com.mobili.backend.shared.mobiliError.exception.MobiliErrorCode;
import com.mobili.backend.shared.mobiliError.exception.MobiliException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class UserReadController {

    private final UserRepository userRepository;
    private final UserMapper userMapper; // Injection via constructeur (Lombok)

    @GetMapping
    public List<ProfileDTO> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toProfileDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProfileDTO getOne(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new MobiliException(
                        MobiliErrorCode.RESOURCE_NOT_FOUND,
                        "Utilisateur avec l'ID " + id + " est introuvable."));
        return userMapper.toProfileDto(user);
    }

    // Dans UserReadController.java

    @GetMapping("/me")
    // 💡 On utilise hasAnyAuthority pour matcher exactement les chaînes
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_PARTNER', 'USER', 'ADMIN', 'PARTNER')")
    public ProfileDTO getMyProfile(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            throw new MobiliException(MobiliErrorCode.ACCESS_DENIED, "Session expirée ou invalide");
        }

        User user = userRepository.findByLogin(principal.getUsername())
                .orElseThrow(() -> new MobiliException(
                        MobiliErrorCode.RESOURCE_NOT_FOUND,
                        "Utilisateur introuvable."));

        return userMapper.toProfileDto(user);
    }

}