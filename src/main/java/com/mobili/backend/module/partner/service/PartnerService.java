package com.mobili.backend.module.partner.service;

import com.mobili.backend.module.partner.entity.Partner;
import com.mobili.backend.module.partner.repository.PartnerRepository;
import com.mobili.backend.shared.mobiliError.exception.MobiliErrorCode;
import com.mobili.backend.shared.mobiliError.exception.MobiliException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerRepository partenaireRepository;

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
    public Partner save(Partner partenaire) {
        // 1. CAS DE LA CRÉATION
        if (partenaire.getId() == null) {
            if (partenaireRepository.findByEmail(partenaire.getEmail()).isPresent()) {
                throw new MobiliException(
                        MobiliErrorCode.DUPLICATE_RESOURCE,
                        "Un partenaire avec cet email existe déjà.");
            }
            return partenaireRepository.save(partenaire);
        }

        // 2. CAS DE LA MISE À JOUR (Fusion/Merge pour éviter d'écraser avec du null)
        return partenaireRepository.findById(partenaire.getId())
                .map(existing -> {
                    // On ne met à jour que les champs non nuls reçus
                    if (partenaire.getName() != null)
                        existing.setName(partenaire.getName());
                    if (partenaire.getEmail() != null)
                        existing.setEmail(partenaire.getEmail());
                    if (partenaire.getPhone() != null)
                        existing.setPhone(partenaire.getPhone());
                    if (partenaire.getBusinessNumber() != null)
                        existing.setBusinessNumber(partenaire.getBusinessNumber());

                    // PROTECTION LOGO : On garde l'ancien logo si le nouveau n'est pas envoyé
                    if (partenaire.getLogoUrl() != null) {
                        existing.setLogoUrl(partenaire.getLogoUrl());
                    }

                    return partenaireRepository.save(existing);
                })
                .orElseThrow(() -> new MobiliException(
                        MobiliErrorCode.RESOURCE_NOT_FOUND,
                        "Impossible de mettre à jour : Partenaire inexistant"));
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