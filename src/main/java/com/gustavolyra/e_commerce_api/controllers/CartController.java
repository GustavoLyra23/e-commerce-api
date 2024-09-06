package com.gustavolyra.e_commerce_api.controllers;

import com.gustavolyra.e_commerce_api.services.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("v1/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }


    @PostMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    public ResponseEntity<Void> addProductToCart(@PathVariable("productId") UUID productId, @RequestParam("quantity") Integer quantity) {
        cartService.addProductToCart(productId, quantity);
        return ResponseEntity.noContent().build();
    }


}
