package com.gustavolyra.e_commerce_api.controllers;

import com.gustavolyra.e_commerce_api.dto.basket.BasketDto;
import com.gustavolyra.e_commerce_api.services.BasketService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/basket")
@Validated
public class BasketController {

    private final BasketService basketService;

    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    public ResponseEntity<Void> deleteBasket(@PathVariable("id") Long id) {
        basketService.deleteBasketById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    public ResponseEntity<String> addProductToBasket(@PathVariable("id") UUID uuid, @RequestParam("quantity")
    @Valid @NotNull(message = "Quantity must not be null") @Positive(message = "Quantity must be positive") Integer quantity) {

        var response = basketService.addProduct(uuid, quantity);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/checkout")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    public ResponseEntity<String> checkout() {
        var paymentLink = basketService.checkout();
        return ResponseEntity.ok(paymentLink);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webHook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        basketService.webhook(payload, sigHeader);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/info")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    public ResponseEntity<BasketDto> basketInfo() {
        var basketDto = basketService.findBasket();
        return ResponseEntity.ok(basketDto);
    }
}
