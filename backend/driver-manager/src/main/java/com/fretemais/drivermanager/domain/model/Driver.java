package com.fretemais.drivermanager.domain.model;

import com.fretemais.drivermanager.domain.enums.VehicleType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@Table(name = "drivers")
@AllArgsConstructor
@NoArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "driver_vehicle_types",
            joinColumns =
    @JoinColumn(name = "driver_id"))
    private List<VehicleType> vehicleType;

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
    private String city;
    private String state;
}
