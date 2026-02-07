package com.fretemais.drivermanager.application.mappers;

import com.fretemais.drivermanager.application.dtos.DriverSummaryDTO;
import com.fretemais.drivermanager.application.dtos.DriverRequestDTO;
import com.fretemais.drivermanager.application.dtos.DriverResponseDTO;
import com.fretemais.drivermanager.domain.model.Driver;
import org.springframework.stereotype.Component;

@Component
public class DriverMapper {

    public Driver toEntity(DriverRequestDTO driverDto){
        if (driverDto == null) return null;

        return Driver.builder()
                .name(driverDto.name())
                .email(driverDto.email())
                .phoneNumber(driverDto.phone())
                .cpf(driverDto.cpf())
                .cnh(driverDto.cnh())
                .city(driverDto.city())
                .state(driverDto.state())
                .available(true)
                .vehicleType(driverDto.vehicleTypes())
                .build();
    }

    public DriverResponseDTO toResponse (Driver driver){
        if (driver == null) return null;

        return DriverResponseDTO.builder()
                .id(driver.getId())
                .name(driver.getName())
                .email(driver.getEmail())
                .phone(driver.getPhoneNumber())
                .cpf(driver.getCpf())
                .cnh(driver.getCnh())
                .city(driver.getCity())
                .state(driver.getState())
                .available(driver.isAvailable())
                .vehicleTypes(driver.getVehicleType())
                .build();
    }

    public DriverSummaryDTO toSummary(Driver driver) {
        if (driver == null) return null;

        return DriverSummaryDTO.builder()
                .id(driver.getId())
                .name(driver.getName())
                .phone(driver.getPhoneNumber())
                .city(driver.getCity())
                .state(driver.getState())
                .available(driver.isAvailable())
                .vehicleTypes(driver.getVehicleType())
                .build();
    }
}
