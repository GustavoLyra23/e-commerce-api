package com.gustavolyra.e_commerce_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDto(@Email String email,
                             @NotBlank(message = "password can't be blank") String password) {
}
