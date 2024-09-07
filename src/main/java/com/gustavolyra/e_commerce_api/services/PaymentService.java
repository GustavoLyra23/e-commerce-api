package com.gustavolyra.e_commerce_api.services;

import com.gustavolyra.e_commerce_api.entities.User;

public interface PaymentService {

    String createPaymentLink(User user);


}
