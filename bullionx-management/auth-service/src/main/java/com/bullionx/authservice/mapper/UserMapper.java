package com.bullionx.authservice.mapper;

import com.bullionx.authservice.dto.UserRequestDTO;
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

    public static User toModel(UserRequestDTO userRequestDTO) {

        User user = new User();
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(userRequestDTO.getPassword());
        return user;
    }
}
