package com.mobili.backend.module.transport.service;

import com.mobili.backend.module.transport.entity.Company;
import com.mobili.backend.module.transport.repository.CompanyRepository;
import com.mobili.backend.shared.mobiliError.exception.MobiliErrorCode;
import com.mobili.backend.shared.mobiliError.exception.MobiliException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    // Dans ton Service
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/.uploads/";

    private final CompanyRepository companyRepository;

    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    public Company findById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new MobiliException(
                        MobiliErrorCode.RESOURCE_NOT_FOUND,
                        "Compagnie introuvable (ID: " + id + ")"));
    }

    @Transactional
    public Company saveWithLogo(Company company, MultipartFile logoFile) {
        // 1. Gestion de l'Update : conserver l'ancien logo si aucun nouveau n'est
        // fourni
        if (company.getId() != null && (logoFile == null || logoFile.isEmpty())) {
            companyRepository.findById(company.getId()).ifPresent(existing -> {
                // On récupère l'URL déjà stockée en base
                company.setLogoUrl(existing.getLogoUrl());
            });
        }
        // 2. Gestion de l'Upload (Create ou Nouvel Update)
        else if (logoFile != null && !logoFile.isEmpty()) {
            try {
                // Utilisation de ton dossier caché .uploads
                String subFolder = "companies/";
                Path uploadPath = Paths.get(System.getProperty("user.dir"), ".uploads", subFolder);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Génération du nom unique
                String fileName = System.currentTimeMillis() + "_" + logoFile.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);

                Files.copy(logoFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // On stocke le chemin relatif pour que le Front puisse le lire via WebConfig
                company.setLogoUrl("/uploads/" + subFolder + fileName);

            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de la sauvegarde du logo", e);
            }
        }

        return companyRepository.save(company);
    }

    @Transactional
    public void delete(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new MobiliException(
                    MobiliErrorCode.RESOURCE_NOT_FOUND,
                    "Impossible de supprimer : Compagnie introuvable");
        }
        companyRepository.deleteById(id);
    }
}