package com.gustavolyra.e_commerce_api.controllers;

import com.gustavolyra.e_commerce_api.dto.user.UserDtoResponse;
import com.gustavolyra.e_commerce_api.dto.user.UserRequestDto;
import com.gustavolyra.e_commerce_api.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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

    @PostMapping("/create")
    public ResponseEntity<UserDtoResponse> create(@Valid @RequestBody UserRequestDto userDto) {
        var user = userService.createUser(userDto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri();
        return ResponseEntity.created(uri).body(user);
    }


}
