package com.gustavolyra.e_commerce_api.services.exceptions;

public class PaymentWebHookException extends RuntimeException {

    public PaymentWebHookException(String msg) {
        super(msg);
    }

}
