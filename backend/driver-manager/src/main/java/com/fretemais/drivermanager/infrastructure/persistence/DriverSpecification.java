package com.fretemais.drivermanager.infrastructure.persistence;

import com.fretemais.drivermanager.domain.enums.VehicleType;
import com.fretemais.drivermanager.domain.model.Driver;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.List;

public class DriverSpecification {

    public static Specification<Driver> filterBy(String text, String state, String city, List<VehicleType> vehicles) {
        return Specification
                .where(hasText(text))
                .and(hasState(state))
                .and(hasCity(city))
                .and(hasVehicles(vehicles));
    }

    private static Specification<Driver> hasText (String text){
        return (root, query, cb) -> {
            if (!StringUtils.hasText(text)) return null;

            String likePattern = "%" + text.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("name")), likePattern),
                    cb.like(cb.lower(root.get("email")), likePattern),
                    cb.like(cb.lower(root.get("cpf")), likePattern),
                    cb.like(cb.lower(root.get("cnh")), likePattern),
                    cb.like(cb.lower(root.get("phoneNumber")), likePattern)
            );
        };
    }

    private static Specification<Driver> hasState(String state){
        return (root, query, cb) -> {
            if (!StringUtils.hasText(state)) return null;

            return cb.equal(cb.lower(root.get("state")), state.toLowerCase());
        };
    }

    private static Specification<Driver> hasCity(String city){
        return (root, query, cb) -> {
            if (!StringUtils.hasText(city)) return null;

            String likePattern = "%" + city.toLowerCase() + "%";

            return cb.like(cb.lower(root.get("city")), likePattern);
        };
    }

    private static Specification<Driver> hasVehicles(List<VehicleType> vehicles){
        return (root, query, cb) -> {
            if (vehicles == null || vehicles.isEmpty()) return null;

            return root.join("vehicleType").in(vehicles);
        };
    }
}
