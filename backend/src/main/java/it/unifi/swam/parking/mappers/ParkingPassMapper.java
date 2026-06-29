package it.unifi.swam.parking.mappers;

import it.unifi.swam.parking.dtos.ParkingPassDTO;
import it.unifi.swam.parking.model.ParkingPass;
import it.unifi.swam.parking.model.User;
import java.util.ArrayList;

public class ParkingPassMapper {

    public static ParkingPassDTO toDTO(ParkingPass pass) {
        if (pass == null)
            return null;
        return new ParkingPassDTO(
                pass.getId(),
                pass.getIssueDate(),
                pass.getExpiryDate(),
                pass.getZone(),
                pass.getOwner() != null ? pass.getOwner().getId() : null,
                new ArrayList<>(pass.getLicensePlates()));
    }

    public static ParkingPass toEntity(ParkingPassDTO dto, User owner) {
        if (dto == null)
            return null;
        ParkingPass pass = new ParkingPass(
                dto.getIssueDate(),
                dto.getExpiryDate(),
                dto.getZone(),
                owner);
        if (dto.getLicensePlates() != null) {
            pass.setLicensePlates(new ArrayList<>(dto.getLicensePlates()));
        }
        return pass;
    }
}
