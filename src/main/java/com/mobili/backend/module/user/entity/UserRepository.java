package com.mobili.backend.module.user.entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Pour l'authentification Spring Security
    Optional<User> findByLogin(String login);

    // Pour vérifier les doublons lors de l'inscription
    Optional<User> findByEmail(String email);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);
}