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
public class StripePaymentService implements PaymentService {

    private final UserService userService;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    public StripePaymentService(UserService userService) {
        this.userService = userService;
    }

    @Transactional()
    @Override
    public String createPaymentLink(User user) {
        try {
            Double price = user.getCart().getItems().stream()
                    .map(x -> x.getProduct().getPrice() * x.getQuantity()).
                    reduce(0.0, Double::sum);

            Stripe.apiKey = stripeApiKey;
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:8080")
                    .putMetadata("cartId", user.getCart().getId().toString())
                    .addLineItem(SessionCreateParams.LineItem.builder().setQuantity(1L)
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder().setCurrency("usd")
                                    .setUnitAmountDecimal(BigDecimal.valueOf(price * 100.0))
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName("Cart payment").build()).build()).build()).build();
            Session session = Session.create(params);
            return session.getUrl();
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }


}
