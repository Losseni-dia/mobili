package com.mobili.backend.module.transport.repository;

import com.mobili.backend.module.transport.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    // Lister tous les bus d'une société donnée
    List<Vehicle> findByCompanyId(Long companyId);
}