package com.fretemais.drivermanager.application.dtos;

import com.fretemais.drivermanager.domain.enums.VehicleType;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record DriverListDTO(
        UUID id,
        String name,
        String email,
        String phone,
        String city,
        String state,
        boolean available,
        List<VehicleType> vehicleTypes
) {}

