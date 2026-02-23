package com.mobili.backend.module.transport.service;

import com.mobili.backend.module.transport.entity.Company;
import com.mobili.backend.module.transport.repository.CompanyRepository;
import com.mobili.backend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    public Company findById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compagnie introuvable (ID: " + id + ")"));
    }

    @Transactional
    public Company save(Company company) {
        return companyRepository.save(company);
    }

    @Transactional
    public void delete(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Impossible de supprimer : Compagnie introuvable");
        }
        companyRepository.deleteById(id);
    }
}