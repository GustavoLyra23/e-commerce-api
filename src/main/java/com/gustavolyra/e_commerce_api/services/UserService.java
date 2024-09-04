package com.gustavolyra.e_commerce_api.services;

import com.gustavolyra.e_commerce_api.dto.user.UserDtoResponse;
import com.gustavolyra.e_commerce_api.dto.user.UserRequestDto;
import com.gustavolyra.e_commerce_api.entities.User;
import com.gustavolyra.e_commerce_api.repositories.RoleRepository;
import com.gustavolyra.e_commerce_api.repositories.UserRepository;
import com.gustavolyra.e_commerce_api.services.exceptions.DatabaseConflictException;
import com.gustavolyra.e_commerce_api.services.exceptions.ForbiddenException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGeneratorService tokenGeneratorService;

    public UserService(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, TokenGeneratorService tokenGeneratorService) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGeneratorService = tokenGeneratorService;
    }

    @Transactional(readOnly = true)
    public String login(UserRequestDto userDto) {
        var user = userRepository.findByEmail(userDto.email()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (passwordEncoder.matches(passwordEncoder.encode(userDto.password()), user.getPassword())) {
            throw new ForbiddenException("Invalid credentials");
        }
        return tokenGeneratorService.generateToken(user);
    }

    @Transactional()
    public UserDtoResponse createUser(UserRequestDto userDto) {
        if (userRepository.existsByEmail(userDto.email())) {
            throw new DatabaseConflictException("Email already exists");
        }

        User user = new User(userDto.email(), passwordEncoder.encode(userDto.password()));
        user.addRole(roleRepository.getReferenceById(1L));
        user = userRepository.save(user);
        return new UserDtoResponse(user);
    }
}
