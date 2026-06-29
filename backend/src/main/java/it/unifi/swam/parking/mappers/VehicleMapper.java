package it.unifi.swam.parking.mappers;

import it.unifi.swam.parking.dtos.VehicleDTO;
import it.unifi.swam.parking.model.User;
import it.unifi.swam.parking.model.Vehicle;

public class VehicleMapper {

    public static VehicleDTO toDTO(Vehicle vehicle) {
        if (vehicle == null)
            return null;
        return new VehicleDTO(
                vehicle.getId(),
                vehicle.getLicensePlate(),
                vehicle.getModel(),
                vehicle.getColor(),
                vehicle.getVehicleType(),
                vehicle.getEngineType(),
                vehicle.getOwner() != null ? vehicle.getOwner().getId() : null);
    }

    public static Vehicle toEntity(VehicleDTO dto, User owner) {
        if (dto == null)
            return null;
        return new Vehicle(
                dto.getLicensePlate(),
                dto.getModel(),
                dto.getColor(),
                dto.getVehicleType(),
                dto.getEngineType(),
                owner);
    }
}
