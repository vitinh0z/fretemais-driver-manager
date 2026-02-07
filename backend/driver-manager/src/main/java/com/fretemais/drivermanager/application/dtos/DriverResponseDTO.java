package com.fretemais.drivermanager.application.dtos;

import com.fretemais.drivermanager.domain.enums.VehicleType;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record DriverResponseDTO(
        UUID id,
        String name,
        String email,
        String phone,
        String cpf,
        String cnh,
        String city,
        String state,
        boolean available,
        List<VehicleType> vehicleTypes
) {}
