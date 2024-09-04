package com.gustavolyra.e_commerce_api.controllers.handler;

import com.gustavolyra.e_commerce_api.dto.FieldError;
import com.gustavolyra.e_commerce_api.dto.StandardError;
import com.gustavolyra.e_commerce_api.dto.ValidationError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ValidationError validationError = new ValidationError(Instant.now(), status.value(), "Validation error", request.getRequestURI());
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            validationError.addFieldError(new FieldError(error.getField(), error.getDefaultMessage()));
        });
        return ResponseEntity.status(status).body(validationError);
    }


}
