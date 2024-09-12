package com.gustavolyra.e_commerce_api.dto.basket;

import com.gustavolyra.e_commerce_api.entities.BasketItem;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
public final class BasketItemDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Long id;
    private final String productName;
    private final Double price;
    private final Integer quantity;

    public BasketItemDto(BasketItem basketItem) {
        id = basketItem.getId();
        productName = basketItem.getProduct().getName();
        price = basketItem.getProduct().getPrice();
        quantity = basketItem.getQuantity();
    }

}
