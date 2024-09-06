package com.gustavolyra.e_commerce_api.services;

import com.gustavolyra.e_commerce_api.entities.Cart;
import com.gustavolyra.e_commerce_api.entities.CartItem;
import com.gustavolyra.e_commerce_api.entities.User;
import com.gustavolyra.e_commerce_api.enums.CartStatus;
import com.gustavolyra.e_commerce_api.repositories.CartItemRepository;
import com.gustavolyra.e_commerce_api.repositories.CartRepository;
import com.gustavolyra.e_commerce_api.repositories.ProductRepository;
import com.gustavolyra.e_commerce_api.repositories.UserRepository;
import com.gustavolyra.e_commerce_api.services.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository, UserRepository userRepository, UserService userService, CartRepository cartRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void addProductToCart(UUID productId, int quantity) {
        var user = userService.findUserFromAuthenticationContext();
        Cart cart = user.getCart();

        if (cart == null || cart.getStatus().equals(CartStatus.PAID)) {
            cart = createNewCart(user);
        }

        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getUuid().equals(productId))
                .findFirst()
                .orElse(new CartItem());

        item.setCart(cart);
        item.setProduct(product);

        item.setQuantity(item.getQuantity() == null ? quantity : item.getQuantity() + quantity);

        if (!cart.getItems().contains(item)) {
            cart.getItems().add(item);
        }
        cartItemRepository.save(item);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Cart createNewCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setStatus(CartStatus.OPEN);
        user.setCart(cart);
        cart = cartRepository.save(cart);
        userRepository.save(user);
        return cart;
    }

}
