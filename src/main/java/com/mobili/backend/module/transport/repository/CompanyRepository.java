package com.mobili.backend.module.transport.repository;

import com.mobili.backend.module.transport.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    // Trouver toutes les compagnies d'un pays spécifique (ex: "CI", "SN")
    List<Company> findByCountry(String country);
}