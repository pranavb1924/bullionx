package com.bullionx.authservice.service;

import com.bullionx.authservice.dto.UserRequestDTO;
import com.bullionx.authservice.dto.UserResponseDTO;
import com.bullionx.authservice.exceptions.EmailAlreadyExistsException;
import com.bullionx.authservice.mapper.UserMapper;
import com.bullionx.authservice.model.User;
import com.bullionx.authservice.repository.UserRepository;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.bullionx.authservice.mapper.UserMapper.toModel;

@Service

@Getter
@Setter
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponseDTO> getUsers(){
        List<User> users = userRepository.findAll();
        List<UserResponseDTO> userResponseDTO = users.stream().map(UserMapper :: toUserResponseDTO).toList();
        return userResponseDTO;
    }

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        User user = new User();
        user = toModel(userRequestDTO);

        if(userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists "+userRequestDTO.getEmail());
        }
        user = userRepository.save(user);
        return UserMapper.toUserResponseDTO(user);
    }
}
