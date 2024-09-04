package com.gustavolyra.e_commerce_api.controllers;

import com.gustavolyra.e_commerce_api.dto.UserRequestDto;
import com.gustavolyra.e_commerce_api.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody UserRequestDto userDto) {
        var token = userService.login(userDto);
        return ResponseEntity.ok(Map.of("token", token));
    }


}
