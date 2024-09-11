package com.gustavolyra.e_commerce_api.services.exceptions;

public class PaymentCreationException extends RuntimeException {
    public PaymentCreationException(String message) {
        super(message);
    }
}
