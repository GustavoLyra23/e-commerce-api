package com.gustavolyra.e_commerce_api.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
public class ValidationError extends StandardError {

    private Set<FieldError> fieldErrors = new HashSet<>();

    public ValidationError(Instant instant, Integer status, String error, String path) {
        super(instant, status, error, path);
    }


    public void addFieldError(FieldError fieldError) {
        fieldErrors.add(fieldError);
    }

}
