package com.mobili.backend.module.partner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mobili.backend.module.partner.entity.Partner;

import java.util.Optional;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {

    // Pour vérifier si une société existe déjà avec cet email
    Optional<Partner> findByEmail(String email);

    // Pour l'Admin : lister uniquement les sociétés actives
    Iterable<Partner> findAllByEnabledTrue();
}