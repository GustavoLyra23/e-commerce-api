package com.gustavolyra.e_commerce_api.services.exceptions;

public class WebHookException extends RuntimeException {
    public WebHookException(String message) {
        super(message);
    }
}
