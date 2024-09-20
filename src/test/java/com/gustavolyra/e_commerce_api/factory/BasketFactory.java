package com.gustavolyra.e_commerce_api.factory;

import com.gustavolyra.e_commerce_api.entities.Basket;

public class BasketFactory {

    public static Basket createBasket() {
        Basket basket = new Basket();
        basket.setUser(UserFactory.createAdminUser());
        basket.setId(1L);
        return basket;
    }


}
