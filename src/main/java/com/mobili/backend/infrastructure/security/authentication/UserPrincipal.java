package com.mobili.backend.infrastructure.security.authentication;

import com.mobili.backend.module.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    @Getter
    private final User user; // L'entité User complète pour accès aux données

    /**
     * Retourne l'ID du partenaire associé à l'utilisateur.
     * Nécessite que la relation @OneToOne soit définie dans l'entité User.
     */
    public Long getPartnerId() {
        // Cette ligne fonctionnera dès que tu auras ajouté :
        // @OneToOne(mappedBy = "owner") private Partner partner; dans User.java
        return (user.getPartner() != null) ? user.getPartner().getId() : null;
    }

    /**
     * Extrait les rôles et ajoute le préfixe ROLE_ indispensable pour .hasRole()
     */
    // Dans UserPrincipal.java
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getRoles() == null)
            return Collections.emptyList(); // 💡 JAMAIS DE NULL ICI
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}