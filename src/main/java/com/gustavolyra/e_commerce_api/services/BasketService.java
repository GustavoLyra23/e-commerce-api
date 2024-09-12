package com.gustavolyra.e_commerce_api.services;

import com.gustavolyra.e_commerce_api.entities.Basket;
import com.gustavolyra.e_commerce_api.entities.BasketItem;
import com.gustavolyra.e_commerce_api.repositories.BasketItemRepository;
import com.gustavolyra.e_commerce_api.repositories.BasketRepository;
import com.gustavolyra.e_commerce_api.repositories.ProductRepository;
import com.gustavolyra.e_commerce_api.services.exceptions.InsufficientStockException;
import com.gustavolyra.e_commerce_api.services.exceptions.PaymentCreationException;
import com.gustavolyra.e_commerce_api.services.exceptions.PaymentWebHookException;
import com.gustavolyra.e_commerce_api.services.exceptions.ResourceNotFoundException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
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

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteBasketById(Long id) {
        boolean exists = basketItemRepository.existsById(id);
        if (!exists) {
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

        //verifies if the product(basketItem) already exist in the basket
        var basketItem = basket.getBasketItems()
                .stream().filter(x -> x.getProduct().getUuid().equals(uuid)).findFirst().orElse(new BasketItem());

        if (basketItem.getProduct() == null) {
            basketItem.setBasket(basket);
            basketItem.setProduct(product);
        }

        basketItem.setQuantity((basketItem.getQuantity() == null) ? quantity : quantity + basketItem.getQuantity());
        basketItemRepository.save(basketItem);
        return "Added product with sucess";
    }

    public String checkout() {
        var user = userService.findUserFromAuthenticationContext();
        if (user.getBasket() == null) {
            throw new ResourceNotFoundException("Invalid checkout");
        }

        var paymentLink = paymentService.createPaymentLink(user);
        if (paymentLink != null) {
            return paymentLink;
        } else {
            throw new PaymentCreationException("Could not create payment");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void webhook(String payload, String header) {
        try {
            Event event = Webhook.constructEvent(payload, header, stripeSecret);
            var basketId = findBasketIdFromWebHook(event);

            if (basketId != null) {
                var basket = basketRepository.findById(Long.valueOf(basketId));
                //loops through the basketItems list to decrease the stock of the products that where in the basket
                if (basket.isPresent()) {
                    basket.get().getBasketItems().forEach(item -> {
                        var product = item.getProduct();
                        product.setStock(product.getStock() - item.getQuantity());
                        productRepository.save(product);
                    });
                    basketRepository.deleteById(Long.valueOf(basketId));
                }
            }
        } catch (Exception e) {
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


}
