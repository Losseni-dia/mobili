package com.mobili.backend.module.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mobili.backend.infrastructure.security.token.JwtService;
import com.mobili.backend.module.user.dto.ProfileDTO;
import com.mobili.backend.module.user.dto.RegisterDTO;
import com.mobili.backend.module.user.dto.login.AuthResponse;
import com.mobili.backend.module.user.dto.login.LoginRequest;
import com.mobili.backend.module.user.dto.mapper.UserMapper;
import com.mobili.backend.module.user.entity.User;
import com.mobili.backend.module.user.entity.UserRepository;
import com.mobili.backend.module.user.service.UserService;
import com.mobili.backend.shared.mobiliError.exception.MobiliErrorCode;
import com.mobili.backend.shared.mobiliError.exception.MobiliException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final UserService userService;


    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        User user = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new MobiliException(MobiliErrorCode.RESOURCE_NOT_FOUND, "Utilisateur non trouvé"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new MobiliException(MobiliErrorCode.INVALID_CREDENTIALS, "Mot de passe incorrect");
        }

        String token = jwtService.generateToken(user);
        System.out.println("TOKEN GÉNÉRÉ POUR JWT.IO : " + token);
        return new AuthResponse(token, user.getLogin(), user.getId());
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileDTO register(
            @RequestPart("user") @Valid RegisterDTO dto,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {

        User user = userMapper.toEntity(dto);
        User savedUser = userService.registerUser(user, avatar);
        return userMapper.toProfileDto(savedUser);
    }
}