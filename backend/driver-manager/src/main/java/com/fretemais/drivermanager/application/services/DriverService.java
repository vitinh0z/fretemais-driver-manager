package com.fretemais.drivermanager.application.services;

import com.fretemais.drivermanager.application.dtos.DriverRequestDTO;
import com.fretemais.drivermanager.application.dtos.DriverResponseDTO;
import com.fretemais.drivermanager.application.mappers.DriverMapper;
import com.fretemais.drivermanager.domain.enums.VehicleType;
import com.fretemais.drivermanager.domain.model.Driver;
import com.fretemais.drivermanager.infrastructure.persistence.DriverRepository;
import com.fretemais.drivermanager.infrastructure.persistence.DriverSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    @Transactional
    public DriverResponseDTO create(DriverRequestDTO dto) {
        if (driverRepository.existsByEmail(dto.email())) throw new IllegalArgumentException("Email já cadastrado");
        if (driverRepository.existsByCpf(dto.cpf())) throw new IllegalArgumentException("CPF já cadastrado");
        if (driverRepository.existsByCnh(dto.cnh())) throw new IllegalArgumentException("CNH já cadastrada");

        Driver entity = driverMapper.toEntity(dto);
        Driver saved = driverRepository.save(entity);
        return driverMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<DriverResponseDTO> list (String text, String state, String city, List<VehicleType> vehicles,
                                         Pageable pageable){

        var specification = DriverSpecification.filterBy(text, state, city, vehicles);

        return driverRepository.findAll(specification, pageable)
                .map(driverMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public DriverResponseDTO getById (UUID id){
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Motorista não encontrado"));

        return driverMapper.toResponse(driver);
    }

    @Transactional
    public void deleteById (UUID id){
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Motorista não encontrado"));

        driverRepository.delete(driver);
    }

    @Transactional
    public DriverResponseDTO updateById (UUID id, DriverRequestDTO newDriver){
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Motorista não encontrado"));

        if (!driver.getEmail().equals(newDriver.email()) && driverRepository.existsByEmail(newDriver.email()))
            throw new IllegalArgumentException("Email já cadastrado");

        if (!driver.getCpf().equals(newDriver.cpf()) && driverRepository.existsByCpf(newDriver.cpf()))
            throw new IllegalArgumentException("CPF já cadastrado");

        if (!driver.getCnh().equals(newDriver.cnh()) && driverRepository.existsByCnh(newDriver.cnh()))
            throw new IllegalArgumentException("CNH já cadastrada");

        driver.setName(newDriver.name());
        driver.setEmail(newDriver.email());
        driver.setPhoneNumber(newDriver.phone());
        driver.setCpf(newDriver.cpf());
        driver.setCnh(newDriver.cnh());
        driver.setCity(newDriver.city());
        driver.setState(newDriver.state());
        driver.setVehicleType(newDriver.vehicleTypes());

        Driver updated = driverRepository.save(driver);
        return driverMapper.toResponse(updated);
    }
}
