package com.gustavolyra.e_commerce_api.services;

import com.gustavolyra.e_commerce_api.entities.User;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class StripePaymentService implements Payment {

    @Value("${stripe.api.key}")
    private String apiKey;
    private final UserService userService;

    public StripePaymentService(UserService userService) {
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    @Override
    public String createPaymentLink(User user) {
        try {
            Double price = user.getBasket().getBasketItems().stream()
                    .map(x -> x.getProduct().getPrice() * x.getQuantity()).reduce(0.0, Double::sum);

            Stripe.apiKey = apiKey;
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:8080")
                    .putMetadata("basketId", user.getBasket().getId().toString())
                    .addLineItem(SessionCreateParams.LineItem.builder().setQuantity(1L)
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder().setCurrency("usd")
                                    .setUnitAmountDecimal(BigDecimal.valueOf(price * 100.0))
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName("Cart payment").build()).build()).build()).build();
            return Session.create(params).getUrl();
        } catch (StripeException e) {
            return null;
        }
    }


}

