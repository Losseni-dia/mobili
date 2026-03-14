package com.mobili.backend.module.user.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mobili.backend.module.user.entity.User;
import com.mobili.backend.module.user.repository.UserRepository;
import com.mobili.backend.module.user.role.Role;
import com.mobili.backend.module.user.role.RoleRepository;
import com.mobili.backend.module.user.role.UserRole;
import com.mobili.backend.shared.mobiliError.exception.MobiliErrorCode;
import com.mobili.backend.shared.mobiliError.exception.MobiliException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.mobili.backend.shared.sharedService.UploadService uploadService; // On réutilise ton service !

    public User findById(Long id) {
        // 💡 On utilise la méthode avec FETCH pour éviter la
        // LazyInitializationException
        return userRepository.findByIdWithEverything(id)
                .orElseThrow(() -> new MobiliException(
                        MobiliErrorCode.RESOURCE_NOT_FOUND,
                        "Utilisateur introuvable (ID: " + id + ")"));
    }

    public List<User> findAllUsers() {
        // On récupère tout simplement les entités
        return userRepository.findAll();
    }

    @Transactional
    public User registerUser(User user, MultipartFile avatarFile) {
        // Vérification des doublons
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new MobiliException(MobiliErrorCode.DUPLICATE_RESOURCE, "Cet email est déjà utilisé.");
        }
        if (userRepository.existsByLogin(user.getLogin())) {
            throw new MobiliException(MobiliErrorCode.DUPLICATE_RESOURCE, "Ce login est déjà utilisé.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);

        assignRoles(user, Collections.singleton(UserRole.USER));

        // Utilisation du service partagé pour l'avatar
        if (avatarFile != null && !avatarFile.isEmpty()) {
            // CHANGEMENT ICI : On passe "users" au lieu de "avatars"
            String path = uploadService.saveImage(avatarFile, "users");
            user.setAvatarUrl(path);
        }

        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, User updatedInfo, Set<UserRole> roleNames, MultipartFile avatarFile) {
        User existingUser = findById(id);

        // Mise à jour des infos de base
        existingUser.setFirstname(updatedInfo.getFirstname());
        existingUser.setLastname(updatedInfo.getLastname());
        existingUser.setEmail(updatedInfo.getEmail());
        existingUser.setLogin(updatedInfo.getLogin());

        // Hashage du mot de passe seulement s'il est fourni
        if (updatedInfo.getPassword() != null && !updatedInfo.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(updatedInfo.getPassword()));
        }

        // Gestion des rôles (si passés, sinon on garde les anciens)
        if (roleNames != null && !roleNames.isEmpty()) {
            assignRoles(existingUser, roleNames);
        }

        // Gestion de l'avatar dans le dossier "users" (configuré dans ton YAML)
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String path = uploadService.saveImage(avatarFile, "users");
            existingUser.setAvatarUrl(path);
        }

        return userRepository.save(existingUser);
    }

    public void toggleUserStatus(Long id, boolean enabled) {
        User user = findById(id);
        user.setEnabled(enabled);
        userRepository.save(user);
    }

    private void assignRoles(User user, Set<UserRole> roleNames) {
        Set<Role> roles = roleNames.stream()
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new MobiliException(
                                MobiliErrorCode.RESOURCE_NOT_FOUND, "Rôle " + name + " inexistant")))
                .collect(Collectors.toSet());
        user.setRoles(roles);
    }

    @Transactional
    public User findByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new MobiliException(
                        MobiliErrorCode.RESOURCE_NOT_FOUND,
                        "Utilisateur avec le login " + login + " introuvable."));
    }

}