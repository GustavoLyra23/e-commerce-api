package com.gustavolyra.e_commerce_api.services;

import com.gustavolyra.e_commerce_api.dto.UserRequestDto;
import com.gustavolyra.e_commerce_api.repositories.UserRepository;
import com.gustavolyra.e_commerce_api.services.exceptions.ForbiddenException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGeneratorService tokenGeneratorService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenGeneratorService tokenGeneratorService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGeneratorService = tokenGeneratorService;
    }

    public String login(UserRequestDto userDto) {
        var user = userRepository.findByEmail(userDto.email()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (passwordEncoder.matches(passwordEncoder.encode(userDto.password()), user.getPassword())) {
            throw new ForbiddenException("Invalid credentials");
        }
        return tokenGeneratorService.generateToken(user);
    }


}
