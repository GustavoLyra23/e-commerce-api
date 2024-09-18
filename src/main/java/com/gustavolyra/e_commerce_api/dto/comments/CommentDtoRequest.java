package com.gustavolyra.e_commerce_api.dto.comments;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentDtoRequest(
        @NotBlank(message = "text can't be blank") @Size(min = 10, max = 200, message = "text size must be between 10 or 200")
        String text) {
}
