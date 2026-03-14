package com.mobili.backend.module.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mobili.backend.module.user.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 1. Pour getMyProfile (Recherche par Login avec Fetch)
    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.roles " +
            "LEFT JOIN FETCH u.partner " +
            "WHERE u.login = :login")
    Optional<User> findByLogin(@Param("login") String login);

    // 2. Pour findById (Recherche par ID avec Fetch)
    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.roles " +
            "LEFT JOIN FETCH u.partner " +
            "WHERE u.id = :id")
    Optional<User> findByIdWithEverything(@Param("id") Long id);

    // 3. Utilitaires pour l'inscription et les doublons
    Optional<User> findByEmail(String email);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);

}