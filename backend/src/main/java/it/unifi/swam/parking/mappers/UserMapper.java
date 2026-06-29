package it.unifi.swam.parking.mappers;

import it.unifi.swam.parking.dtos.UserDTO;
import it.unifi.swam.parking.model.User;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        if (user == null)
            return null;
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getDistrict(),
                user.getRole());
    }

    public static User toEntity(UserDTO dto) {
        if (dto == null)
            return null;
        User user = new User(
                dto.getUsername(),
                dto.getPassword(),
                dto.getEmail(),
                dto.getFullName(),
                dto.getDistrict(),
                dto.getRole());
        user.setId(dto.getId());
        return user;
    }
}
