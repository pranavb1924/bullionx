package com.bullionx.authservice.service;

import com.bullionx.authservice.model.User;
import com.bullionx.authservice.repository.UserRepository;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserService {

    UserRepository userRepository;

    public List<UserResponseDTO> getUsers(){

        List<User> users = userRepository.findAll();

        return users;

    }
}
