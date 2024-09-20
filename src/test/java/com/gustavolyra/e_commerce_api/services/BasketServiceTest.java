package com.gustavolyra.e_commerce_api.services;


import com.gustavolyra.e_commerce_api.entities.Basket;
import com.gustavolyra.e_commerce_api.entities.User;
import com.gustavolyra.e_commerce_api.factory.BasketFactory;
import com.gustavolyra.e_commerce_api.factory.UserFactory;
import com.gustavolyra.e_commerce_api.repositories.BasketItemRepository;
import com.gustavolyra.e_commerce_api.repositories.BasketRepository;
import com.gustavolyra.e_commerce_api.repositories.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class BasketServiceTest {

    @Mock
    BasketRepository basketRepository;

    @Mock
    UserService userService;

    @Mock
    ProductRepository productRepository;

    @Mock
    BasketItemRepository basketItemRepository;


    @InjectMocks
    BasketService basketService;
    Long validId;
    Long invalidId;
    User adminUser;
    Basket basket;

    @BeforeEach
    public void setUp() {
        // Arrange
        validId = 1L;
        invalidId = 10000L;
        Mockito.doNothing().when(basketRepository).deleteById(validId);
        basket = BasketFactory.createBasket();
        Mockito.when(basketRepository.findById(validId)).thenReturn(Optional.ofNullable(basket));
        adminUser = UserFactory.createAdminUser();
        Mockito.when(userService.findUserFromAuthenticationContext()).thenReturn(adminUser);
    }


    @Test
    void deleteByIdShouldNotThrowException() {
        // Act
        // Assert
        Assertions.assertDoesNotThrow(() -> basketService.deleteBasketById(validId));
    }


}
