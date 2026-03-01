package com.mobili.backend.module.user.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mobili.backend.module.user.dto.ProfileDTO;
import com.mobili.backend.module.user.dto.mapper.UserMapper;
import com.mobili.backend.module.user.entity.User;
import com.mobili.backend.module.user.entity.UserRepository;
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

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'PARTNER')") // Parenthèses corrigées ici
    public ProfileDTO getMyProfile(@RequestParam String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new MobiliException(
                        MobiliErrorCode.RESOURCE_NOT_FOUND,
                        "Session invalide : le compte '" + login + "' n'existe pas."));

        return userMapper.toProfileDto(user);
    }

}