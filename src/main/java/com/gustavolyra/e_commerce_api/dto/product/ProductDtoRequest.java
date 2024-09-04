package com.gustavolyra.e_commerce_api.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ProductDtoRequest(@NotBlank(message = "name cant't be null") String name,
                                @NotBlank(message = "description cant't be null") String description,
                                @Positive(message = "price must be positive") Double price,
                                @NotBlank(message = "type can't be blank") String type) {
}
