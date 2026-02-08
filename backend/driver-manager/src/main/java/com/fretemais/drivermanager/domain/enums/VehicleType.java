package com.fretemais.drivermanager.domain.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipo de veículo operado pelo motorista")
public enum VehicleType {
    CAR,
    MOTORCYCLE,
    TRUCK
}
