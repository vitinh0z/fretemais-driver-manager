package com.fretemais.drivermanager.infrastructure.persistence;

import com.fretemais.drivermanager.domain.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID>, JpaSpecificationExecutor<Driver> {

    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    boolean existsByCnh(String cnh);
}