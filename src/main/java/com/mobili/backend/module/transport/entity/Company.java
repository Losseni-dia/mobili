
/*La société qui possède les véhicules. */

package com.mobili.backend.module.transport.entity;

import com.mobili.backend.shared.abstractEntity.AbstractEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Company extends AbstractEntity {

    @Column(nullable = false)
    private String name;

    private String logoUrl;

    private String businessNumber;

    @Column(nullable = false)
    private String country; // Pour filtrer les compagnies par pays

    private String phone;
}