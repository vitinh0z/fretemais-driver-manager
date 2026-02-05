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
    @Column(unique = true)
    private String cpf;
    @Column(unique = true)
    private String cnh;
    @Column(unique = true)
    private String phoneNumber;
    @Column(unique = true)
    private String email;
    private boolean available;
}
