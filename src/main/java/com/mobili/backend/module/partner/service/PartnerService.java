package com.mobili.backend.module.partner.service;

import java.security.SecureRandom;
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
import com.mobili.backend.shared.MobiliError.exception.MobiliErrorCode;
import com.mobili.backend.shared.MobiliError.exception.MobiliException;
import com.mobili.backend.shared.sharedService.UploadService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PartnerService {

    private static final char[] REG_CODE_ALPHANUM = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    private static final SecureRandom RANDOM = new SecureRandom();

    private final PartnerRepository partenaireRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UploadService uploadService;

    public Partner getCurrentPartner() {
        Object principal = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserPrincipal)) {
            throw new MobiliException(MobiliErrorCode.ACCESS_DENIED, "Non authentifié");
        }

        UserPrincipal userPrincipal = (UserPrincipal) principal;

        if (userPrincipal.getPartnerId() != null) {
            return partenaireRepository.findById(userPrincipal.getPartnerId())
                    .orElseThrow(
                            () -> new MobiliException(MobiliErrorCode.RESOURCE_NOT_FOUND, "Partenaire non trouvé"));
        }
        Long userId = userPrincipal.getUser().getId();
        return partenaireRepository.findByOwnerId(userId)
                .orElseThrow(() -> new MobiliException(MobiliErrorCode.RESOURCE_NOT_FOUND,
                        "Aucune entreprise liée à cet utilisateur"));
    }

    /**
     * Résout l’entreprise courante (comme {@link #getCurrentPartner()}) et crée
     * un {@link Partner#getRegistrationCode() code} s’il est encore absent (affichage / API).
     */
    @Transactional
    public Partner getCurrentPartnerEnsuringRegistrationCode() {
        Partner p = getCurrentPartner();
        if (p.getRegistrationCode() == null || p.getRegistrationCode().isBlank()) {
            p.setRegistrationCode(generateUniqueRegistrationCode());
            p = partenaireRepository.save(p);
        }
        return p;
    }

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

    @Transactional
    public Partner save(Partner partenaire, MultipartFile logoFile, UserPrincipal principal) {
        if (partenaire.getId() == null) {
            User user = userRepository.findByLogin(principal.getUsername())
                    .orElseThrow(() -> new MobiliException(MobiliErrorCode.RESOURCE_NOT_FOUND, "User non trouvé"));

            partenaire.setOwner(user);

            Role partnerRole = roleRepository.findByName(UserRole.PARTNER).get();
            user.getRoles().add(partnerRole);
            userRepository.save(user);

            handleLogoUpload(partenaire, logoFile);

            if (partenaire.getRegistrationCode() == null || partenaire.getRegistrationCode().isBlank()) {
                partenaire.setRegistrationCode(generateUniqueRegistrationCode());
            }

            return partenaireRepository.save(partenaire);
        }

        return partenaireRepository.findById(partenaire.getId())
                .map(existing -> {
                    existing.setName(partenaire.getName());
                    existing.setEmail(partenaire.getEmail());
                    existing.setPhone(partenaire.getPhone());
                    existing.setBusinessNumber(partenaire.getBusinessNumber());

                    handleLogoUpload(existing, logoFile);

                    return partenaireRepository.save(existing);
                })
                .orElseThrow(() -> new MobiliException(MobiliErrorCode.RESOURCE_NOT_FOUND, "Partenaire introuvable"));
    }

    private String generateUniqueRegistrationCode() {
        for (int attempt = 0; attempt < 20; attempt++) {
            StringBuilder sb = new StringBuilder(8);
            for (int i = 0; i < 8; i++) {
                sb.append(REG_CODE_ALPHANUM[RANDOM.nextInt(REG_CODE_ALPHANUM.length)]);
            }
            String code = sb.toString();
            if (partenaireRepository.findByRegistrationCodeIgnoreCase(code).isEmpty()) {
                return code;
            }
        }
        throw new MobiliException(MobiliErrorCode.VALIDATION_ERROR, "Impossible de générer un code partenaire unique");
    }

    private void handleLogoUpload(Partner partner, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            String path = uploadService.saveImage(file, "partners");
            partner.setLogoUrl(path);
        }
    }

    @Transactional(readOnly = true)
    public Partner findByOwnerId(Long ownerId) {
        return partenaireRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new MobiliException(
                        MobiliErrorCode.RESOURCE_NOT_FOUND,
                        "Aucune entreprise n'est associée à cet utilisateur."));
    }

    @Transactional
    public void fillMissingRegistrationCodes() {
        for (Partner p : partenaireRepository.findAll()) {
            if (p.getRegistrationCode() == null || p.getRegistrationCode().isBlank()) {
                p.setRegistrationCode(generateUniqueRegistrationCode());
                partenaireRepository.save(p);
            }
        }
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
