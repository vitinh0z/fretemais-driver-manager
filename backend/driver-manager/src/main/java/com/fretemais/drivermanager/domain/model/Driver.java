package com.fretemais.drivermanager.domain.model;

import com.fretemais.drivermanager.domain.enums.VehicleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "drivers")
@AllArgsConstructor
@NoArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;
    @CPF
    private String cpf;
    private String cnh;
    private String phoneNumber;
    private String email;
    private boolean available;
}
