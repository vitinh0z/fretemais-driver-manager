package com.fretemais.drivermanager.application.dtos;

import com.fretemais.drivermanager.domain.enums.VehicleType;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record DriverSummaryDTO(
        UUID id,
        String name,
        String phone,
        String city,
        String state,
        List<VehicleType> vehicleTypes,
        boolean available
) {}
