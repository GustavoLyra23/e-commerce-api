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
import com.gustavolyra.e_commerce_api.services.exceptions.WebHookException;
import com.stripe.exception.SignatureVerificationException;
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
public class CartService {


    @Value("${stripe.sign.secret}")
    private String stripeWebhookSecret;

    private final PaymentService paymentService;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(PaymentService paymentService, CartItemRepository cartItemRepository, UserRepository userRepository, UserService userService, CartRepository cartRepository, ProductRepository productRepository) {
        this.paymentService = paymentService;
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

        var product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        CartItem item = cart.getItems().stream().filter(i -> i.getProduct()
                .getUuid().equals(productId)).findFirst().orElse(new CartItem());

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
        cart.setUser(userRepository.getReferenceById(user.getId()));
        cart.setStatus(CartStatus.OPEN);
        user.setCart(cart);
        cart = cartRepository.save(cart);
        return cart;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String checkout() {
        var user = userService.findUserFromAuthenticationContext();
        if (user.getCart() == null) {
            throw new ResourceNotFoundException("No cart found");
        }
        var cart = cartRepository.findById(user.getCart().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart id not found"));
        if (cart.getItems().isEmpty()) {
            throw new ResourceNotFoundException("No items found");
        }
        return paymentService.createPaymentLink(user);
    }

    @Transactional
    public void cartWebHook(String payload, String header) {

        try {
            Event event = Webhook.constructEvent(payload, header, stripeWebhookSecret);

            if ("checkout.session.completed".equals(event.getType())) {
                var cartId = findCartIdFromWebHook(event);
                if (cartId != null) {
                    var cart = cartRepository.findById(Long.valueOf(cartId));
                    if (cart.isPresent()) {
                        cart.get().setStatus(CartStatus.PAID);
                        cartRepository.save(cart.get());
                    }
                }
            }
        } catch (SignatureVerificationException e) {
            throw new WebHookException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String findCartIdFromWebHook(Event event) {
        Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
        if (session != null) {
            Map<String, String> metadata = session.getMetadata();
            return metadata.get("cartId"); // Obtém o valor do 'cartId'
        }
        return null;
    }

}