package com.gustavolyra.e_commerce_api.dto.basket;

import com.gustavolyra.e_commerce_api.entities.Basket;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public final class BasketDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Long id;
    private final List<BasketItemDto> list = new ArrayList<>();

    public BasketDto(Basket basket) {
        id = basket.getId();
        //loops through the basket items and adds them to the list
        basket.getBasketItems().forEach(basketItem -> list.add(new BasketItemDto(basketItem)));
    }

    public BasketDto(Long id) {
        this.id = id;
    }

}
