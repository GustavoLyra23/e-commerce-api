package com.gustavolyra.e_commerce_api.services.exceptions;

public class DatabaseConflictException extends RuntimeException {
    public DatabaseConflictException(String message) {
        super(message);
    }
}
