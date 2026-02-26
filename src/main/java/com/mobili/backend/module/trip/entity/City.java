
/**Essentiel pour définir les points de départ et d'arrivée. */

package com.mobili.backend.module.trip.entity;

import com.mobili.backend.shared.abstractEntity.AbstractEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cities")
@Getter
@Setter
public class City extends AbstractEntity {

    @Column(nullable = false)
    private String cityName;

    @Column(nullable = false)
    private String country;
}