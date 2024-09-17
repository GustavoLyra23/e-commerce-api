package com.gustavolyra.e_commerce_api.services;

import com.gustavolyra.e_commerce_api.dto.basket.BasketDto;
import com.gustavolyra.e_commerce_api.entities.Basket;
import com.gustavolyra.e_commerce_api.entities.BasketItem;
import com.gustavolyra.e_commerce_api.repositories.BasketItemRepository;
import com.gustavolyra.e_commerce_api.repositories.BasketRepository;
import com.gustavolyra.e_commerce_api.repositories.ProductRepository;
import com.gustavolyra.e_commerce_api.services.exceptions.*;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class BasketService {

    @Value("${stripe.sign.secret}")
    private String stripeSecret;

    private final Payment paymentService;
    private final BasketRepository basketRepository;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final BasketItemRepository basketItemRepository;

    public BasketService(Payment paymentService, BasketRepository basketRepository, UserService userService, ProductRepository productRepository, BasketItemRepository basketItemRepository) {
        this.paymentService = paymentService;
        this.basketRepository = basketRepository;
        this.userService = userService;
        this.productRepository = productRepository;
        this.basketItemRepository = basketItemRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteBasketById(Long id) {
        log.info("Received request to delete basket with id: {}", id);
        try {
            boolean exists = basketItemRepository.existsById(id);
            if (!exists) {
                log.error("Basket with id: {} not found", id);
                throw new ResourceNotFoundException("Basket not found");
            }
            basketRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseConflictException("Could not delete basket");
        }
        log.info("Basket with id: {} deleted successfully", id);
    }

    @Transactional
    public String addProduct(UUID uuid, Integer quantity) {
        log.info("Received request to add product with id: {} and quantity: {}", uuid, quantity);
        var product = productRepository.findById(uuid).orElseThrow(() -> {
            log.error("Product with id: {} not found", uuid);
            return new ResourceNotFoundException("Product not found");
        });

        if (product.getStock() < quantity) {
            log.error("Insufficient stock for product with id: " + "{}. Requested quantity: {}, available stock: {}", uuid, quantity, product.getStock());
            throw new InsufficientStockException("Invalid stock quantity");
        }

        var user = userService.findUserFromAuthenticationContext();
        var basket = user.getBasket();
        if (basket == null) {
            log.info("No basket found for user, creating a new basket");
            Basket newBasket = new Basket();
            newBasket.setUser(user);
            basket = basketRepository.save(newBasket);
            log.info("New basket created with id: {}", basket.getId());
        }

        //verifies if the product(basketItem) already exist in the basket
        var basketItem = basket.getBasketItems().stream()
                .filter(x -> x.getProduct().getUuid().equals(uuid))
                .findFirst()
                .orElse(new BasketItem());

        //if the item does not exist it will create a new one
        if (basketItem.getProduct() == null) {
            basketItem.setBasket(basket);
            basketItem.setProduct(product);
            log.info("Adding new product to basket: {}", uuid);
        }

        addQuantityToBasket(basketItem, quantity);

        if (basketItem.getQuantity() > basketItem.getProduct().getStock()) {
            log.error("Insufficient stock for product with id: " + "{}. Requested quantity: {}, available stock: {}", uuid, basketItem.getQuantity(), product.getStock());
            throw new InsufficientStockException("Invalid stock");
        }

        basketItemRepository.save(basketItem);
        log.info("Product with id: {} added to basket with new quantity: {}", uuid, basketItem.getQuantity());
        return "Added product with sucess";
    }

    public String checkout() {
        log.info("Received request for checkout");
        var user = userService.findUserFromAuthenticationContext();
        if (user.getBasket() == null) {
            log.error("No basket found for user during checkout");
            throw new ResourceNotFoundException("Invalid checkout");
        } else if (user.getBasket().getBasketItems().isEmpty()) {
            log.error("Empty basket found for user during checkout");
            throw new ResourceNotFoundException("Empty basket");
        }

        var paymentLink = paymentService.createPaymentLink(user);
        if (paymentLink != null) {
            log.info("Payment link created successfully: {}", paymentLink);
            return paymentLink;
        } else {
            log.error("Failed to create payment link");
            throw new PaymentCreationException("Could not create payment");
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void webhook(String payload, String header) {
        log.info("Received webhook event");
        try {
            Event event = Webhook.constructEvent(payload, header, stripeSecret);
            var basketId = findBasketIdFromWebHook(event);

            if (basketId != null) {
                log.info("Processing webhook for basketId: {}", basketId);
                var basket = basketRepository.findById(Long.valueOf(basketId));

                //loops through the basketItems list to decrease the stock of the products that where in the basket
                if (basket.isPresent()) {
                    basket.get().getBasketItems().forEach(item -> {
                        var product = item.getProduct();
                        product.setStock(product.getStock() - item.getQuantity());
                        productRepository.save(product);
                        log.info("Stock updated for product id: {}. New stock: {}", product.getUuid(), product.getStock());
                    });
                    basketRepository.deleteById(Long.valueOf(basketId));
                    log.info("Basket with id: {} processed and deleted", basketId);
                }
            }
        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage(), e);
            throw new PaymentWebHookException("Error during webhook");
        }
    }

    private String findBasketIdFromWebHook(Event event) {
        Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
        if (session != null) {
            Map<String, String> metadata = session.getMetadata();
            return metadata.get("basketId");
        }
        return null;
    }

    @Transactional()
    public BasketDto findBasket() {
        log.info("Received request to find basket");
        var user = userService.findUserFromAuthenticationContext();
        var basket = user.getBasket();
        if (basket == null) {
            Basket newBasket = new Basket(null, user, null);
            newBasket = basketRepository.save(newBasket);
            return new BasketDto(newBasket.getId());
        }
        log.info("Basket found for user with id: {}", user.getId());
        return new BasketDto(basket);
    }

    private void addQuantityToBasket(BasketItem basketItem, Integer quantity) {
        basketItem.setQuantity((basketItem.getQuantity() == null) ? quantity : quantity + basketItem.getQuantity());
    }


}
