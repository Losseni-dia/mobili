package com.mobili.backend.module.partner.service;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mobili.backend.infrastructure.security.authentication.UserPrincipal;
import com.mobili.backend.module.partner.entity.Partner;
import com.mobili.backend.module.partner.repository.PartnerRepository;
import com.mobili.backend.module.user.entity.User;
import com.mobili.backend.module.user.repository.UserRepository;
import com.mobili.backend.module.user.role.Role;
import com.mobili.backend.module.user.role.RoleRepository;
import com.mobili.backend.module.user.role.UserRole;
import com.mobili.backend.shared.mobiliError.exception.MobiliErrorCode;
import com.mobili.backend.shared.mobiliError.exception.MobiliException;
import com.mobili.backend.shared.sharedService.UploadService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerRepository partenaireRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UploadService uploadService;



    // --- LECTURE ---
    @Transactional(readOnly = true)
    public List<Partner> findAll() {
        return partenaireRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Partner findById(Long id) {
        return partenaireRepository.findById(id)
                .orElseThrow(() -> new MobiliException(
                        MobiliErrorCode.RESOURCE_NOT_FOUND,
                        "Partenaire introuvable (ID: " + id + ")"));
    }

    // --- ÉCRITURE (Sauvegarde & Mise à jour sécurisée) ---
  @Transactional
public Partner save(Partner partenaire, MultipartFile logoFile, UserPrincipal principal) {
    // 1. CAS DE LA CRÉATION
    if (partenaire.getId() == null) {
        User user = userRepository.findByLogin(principal.getUsername())
                .orElseThrow(() -> new MobiliException(MobiliErrorCode.RESOURCE_NOT_FOUND, "User non trouvé"));

        partenaire.setOwner(user);

        Role partnerRole = roleRepository.findByName(UserRole.PARTNER).get();
        user.getRoles().add(partnerRole);
        userRepository.save(user);

        // Gestion du logo à la création
        handleLogoUpload(partenaire, logoFile);

        return partenaireRepository.save(partenaire);
    }

    // 2. CAS DE LA MISE À JOUR
    return partenaireRepository.findById(partenaire.getId())
            .map(existing -> {
                existing.setName(partenaire.getName());
                existing.setEmail(partenaire.getEmail());
                existing.setPhone(partenaire.getPhone());
                existing.setBusinessNumber(partenaire.getBusinessNumber());

                // Gestion du logo à la mise à jour 💡
                handleLogoUpload(existing, logoFile);

                return partenaireRepository.save(existing);
            })
            .orElseThrow(() -> new MobiliException(MobiliErrorCode.RESOURCE_NOT_FOUND, "Partenaire introuvable"));
}

// 💡 Petite méthode privée pour centraliser l'upload dans le bon dossier
private void handleLogoUpload(Partner partner, MultipartFile file) {
    if (file != null && !file.isEmpty()) {
        // On utilise "partners" pour correspondre à ton YAML (mobili.backend.upload.partners)
        String path = uploadService.saveImage(file, "partners"); 
        partner.setLogoUrl(path);
    }
}

    // Dans PartnerService.java

    @Transactional(readOnly = true)
    public Partner findByOwnerId(Long ownerId) {
        return partenaireRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new MobiliException(
                        MobiliErrorCode.RESOURCE_NOT_FOUND,
                        "Aucune entreprise n'est associée à cet utilisateur."));
    }

    @Transactional
    public void toggleStatus(Long id) {
        Partner p = findById(id);
        p.setEnabled(!p.isEnabled());
        partenaireRepository.save(p);
    }

    @Transactional
    public void delete(Long id) {
        if (!partenaireRepository.existsById(id)) {
            throw new MobiliException(
                    MobiliErrorCode.RESOURCE_NOT_FOUND,
                    "Impossible de supprimer : Partenaire inexistant");
        }
        partenaireRepository.deleteById(id);
    }
}