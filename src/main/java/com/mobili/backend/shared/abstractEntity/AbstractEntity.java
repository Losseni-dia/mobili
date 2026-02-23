package com.mobili.backend.shared.abstractEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@MappedSuperclass // Dit à JPA : "Ne crée pas de table pour ça, mais copie ces champs ailleurs"
@EntityListeners(AuditingEntityListener.class) // Active l'auto-remplissage des dates
@Getter
@Setter
public abstract class AbstractEntity {
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}