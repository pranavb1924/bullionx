package com.bullionx.authservice.mapper;

import com.bullionx.authservice.dto.UserResponseDTO;
import com.bullionx.authservice.model.User;

public class UserMapper {

    public static UserResponseDTO toUserResponseDTO(User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setFirstName(user.getFirstName());
        userResponseDTO.setLastName(user.getLastName());
        userResponseDTO.setEmail(user.getEmail());
        return userResponseDTO;
    }
}
