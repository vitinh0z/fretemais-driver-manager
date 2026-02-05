package com.fretemais.drivermanager.domain.model;

import com.fretemais.drivermanager.domain.enums.VehicleType;

public class Driver {

    private String name;
    private VehicleType vehicleType;
    private String cnh;
    private String phoneNumber;
    private String email;
    private boolean available;

    private Driver (
        String name, VehicleType vehicleType, String cnh, 
        String phoneNumber, String email, boolean available
    )
    {

        this.name = name;
        this.vehicleType = vehicleType;
        this.cnh = cnh;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.available = true;
    }  
}
