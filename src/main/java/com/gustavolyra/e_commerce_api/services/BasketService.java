package com.gustavolyra.e_commerce_api.services;

import com.gustavolyra.e_commerce_api.entities.Basket;
import com.gustavolyra.e_commerce_api.entities.BasketItem;
import com.gustavolyra.e_commerce_api.repositories.BasketItemRepository;
import com.gustavolyra.e_commerce_api.repositories.BasketRepository;
import com.gustavolyra.e_commerce_api.repositories.ProductRepository;
import com.gustavolyra.e_commerce_api.services.exceptions.InsufficientStockException;
import com.gustavolyra.e_commerce_api.services.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class BasketService {

    private final BasketRepository basketRepository;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final BasketItemRepository basketItemRepository;

    public BasketService(BasketRepository basketRepository, UserService userService, ProductRepository productRepository, BasketItemRepository basketItemRepository) {
        this.basketRepository = basketRepository;
        this.userService = userService;
        this.productRepository = productRepository;
        this.basketItemRepository = basketItemRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteBasketById(Long id) {
        if (basketItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Basket not found");
        }
        basketRepository.deleteById(id);
    }

    @Transactional
    public String addProduct(UUID uuid, Integer quantity) {
        var product = productRepository.findById(uuid).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (product.getStock() < quantity) {
            throw new InsufficientStockException("Invalid stock quantity");
        }

        var user = userService.findUserFromAuthenticationContext();
        var basket = user.getBasket();
        if (basket == null) {
            Basket newBasket = new Basket();
            newBasket.setUser(user);
            basket = basketRepository.save(newBasket);
        }

        //verifies if the product(item) already exist in the basket
        var basketItem = basket.getBasketItems().stream().filter(x -> x.getProduct().getUuid().equals(uuid)).findFirst().orElse(new BasketItem());

        if (basketItem.getProduct() == null) {
            basketItem.setBasket(basket);
            basketItem.setProduct(product);
        }

        basketItem.setQuantity((basketItem.getQuantity() == null) ? quantity : quantity + basketItem.getQuantity());
        basketItemRepository.save(basketItem);
        return "Added product with sucess";
    }


}
