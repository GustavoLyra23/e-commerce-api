package com.gustavolyra.e_commerce_api.controllers;

import com.gustavolyra.e_commerce_api.services.BasketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/basket")
public class BasketController {

    private final BasketService basketService;

    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBasket(@PathVariable("id") Long id) {
        basketService.deleteBasketById(id);
        return ResponseEntity.noContent().build();
    }


}
