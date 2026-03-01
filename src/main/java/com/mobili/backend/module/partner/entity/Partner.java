package com.mobili.backend.module.partner.entity;

import com.mobili.backend.shared.abstractEntity.AbstractEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "partners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Partner extends AbstractEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;// Email officiel de la société

    private String logoUrl;

    private String businessNumber;

    private String phone;

    private boolean enabled = true; // Permet à l'admin de bloquer la société

    
}