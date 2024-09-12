package com.gustavolyra.e_commerce_api.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

public record UserRequestDto(@Email(message = "Invalid email format") String email,
                             @NotBlank(message = "password can't be blank") String password) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
