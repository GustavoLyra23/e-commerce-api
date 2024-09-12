package com.gustavolyra.e_commerce_api.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

public record ProductDtoRequest(@NotBlank(message = "name cant't be null") String name,
                                @NotBlank(message = "description cant't be null") String description,
                                @Positive(message = "price must be positive") Double price,
                                @NotBlank(message = "type can't be blank") String type,
                                @Positive(message = "stock must be positive") Integer stock,
                                @NotNull(message = "file can't be null") MultipartFile file) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
