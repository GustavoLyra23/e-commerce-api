package com.gustavolyra.e_commerce_api.services;

import com.gustavolyra.e_commerce_api.dto.product.ProductDtoRequest;
import com.gustavolyra.e_commerce_api.dto.product.ProductDtoResponse;
import com.gustavolyra.e_commerce_api.entities.Product;
import com.gustavolyra.e_commerce_api.entities.User;
import com.gustavolyra.e_commerce_api.enums.ProductType;
import com.gustavolyra.e_commerce_api.factory.ProductFactory;
import com.gustavolyra.e_commerce_api.factory.UserFactory;
import com.gustavolyra.e_commerce_api.repositories.ProductRepository;
import com.gustavolyra.e_commerce_api.repositories.UserRepository;
import com.gustavolyra.e_commerce_api.services.exceptions.ForbiddenException;
import com.gustavolyra.e_commerce_api.services.exceptions.ResourceNotFoundException;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.jsonwebtoken.lang.Assert.notNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class ProductServiceTest {

    @Mock
    S3Service s3Service;

    @Mock
    ProductRepository productRepository;

    @Mock
    UserService userService;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    ProductService productService;

    PageImpl<Product> page;
    Product product;
    User userAdmin;
    User nonAdminUser;
    PageRequest pageRequest;
    ProductDtoRequest invalidProductType;
    ProductDtoRequest validProductDtoRequest;
    UUID validProductUUID;
    UUID invalidProductUUID;

    @BeforeEach
    void setUp() throws IOException {
        //ARRANGE
        pageRequest = PageRequest.of(1, 5);
        product = ProductFactory.createProduct();
        page = new PageImpl<>(List.of(product));
        Mockito.when(productRepository.findAll(pageRequest)).thenReturn(page);

        userAdmin = UserFactory.createAdminUser();
        nonAdminUser = UserFactory.createNonAdminUser();

        Mockito.when(userRepository.getReferenceById(userAdmin.getId())).thenReturn(userAdmin);
        Mockito.when(s3Service.addFileToBucket(any())).thenReturn("https://mockImage");
        Mockito.when(productRepository.save(any())).thenReturn(product);
        Mockito.when(userService.findUserFromAuthenticationContext()).thenReturn(userAdmin);
        invalidProductType = ProductFactory.createInvalidTypeProductDtoRequest();
        validProductDtoRequest = ProductFactory.createProductDtoRequest();

        validProductUUID = UUID.fromString("97e630e3-637a-4930-b4e9-8fcf48fda319");
        invalidProductUUID = UUID.fromString("97e630e3-637a-4930-b4e9-8fcf48fda318");

        Mockito.doNothing().when(productRepository).deleteById(validProductUUID);
        Mockito.when(productRepository.findById(validProductUUID)).thenReturn(Optional.ofNullable(product));
        Mockito.when(productRepository.findById(invalidProductUUID)).thenThrow(ResourceNotFoundException.class);
    }

    @Test
    void getAllProductsShouldReturnPageOfProducts() {
        //ACT
        var result = productService.getAllProducts(null, pageRequest);
        //ASSERT
        notNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void createProductShouldReturnProductDtoWhenValidInput() throws IOException {
        //ACT
        var productResult = productService.createProduct(ProductFactory.createProductDtoRequest());
        //ASSERT
        assertNotNull(productResult);
        assertEquals(productResult.getId(), product.getUuid());
        assertEquals(productResult.getName(), product.getName());
        assertEquals(productResult.getStock(), product.getStock());
        assertDoesNotThrow(() -> new IllegalArgumentException());
    }

    @Test
    void createProductShouldThrowExceptionWhenInvalidType() throws IOException {
        //ACT
        //ASSERT
        assertThrows(BadRequestException.class, () -> productService.createProduct(invalidProductType));
    }

    @Test
    void deleteByIdShouldDoNothingWhenIdExists() {
        //ACT
        //ASSERT
        assertDoesNotThrow(() -> productService.deleteProductById(validProductUUID));
    }

    @Test
    void deleteByIdShouldThrowExceptionWhenIdDoesNotExist() {
        //ACT
        //ASSERT
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProductById(invalidProductUUID));
    }

    @Test
    void deleteByIdShouldThrowExceptionWhenNonAdminUserTriesToDeleteOtherUsersProduct() {
        //ARRANGE
        Mockito.when(userService.findUserFromAuthenticationContext()).thenReturn(nonAdminUser);
        //ACT
        //ASSERT
        assertThrows(ForbiddenException.class, () -> productService.deleteProductById(validProductUUID));
    }

    @Test
    void updateProductShouldReturnProductDtoResponseWhenValidArguments() throws IOException {
        //ACT
        ProductDtoResponse result = productService.updateProduct(validProductUUID, validProductDtoRequest);
        //ASSERT
        assertNotNull(result);
        assertEquals(result.getName(), validProductDtoRequest.name());
        assertEquals(result.getStock(), validProductDtoRequest.stock());
        assertEquals(result.getId(), validProductUUID);
        assertEquals(result.getProductType(), ProductType.valueOf(validProductDtoRequest.type()));
        assertEquals(result.getPictureUrl(), "https://mockImage");
    }

    @Test
    void updateProductShouldThrowExceptionWhenInvalidProductId() {
        //ACT
        //ASSERT
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(invalidProductUUID, validProductDtoRequest));
    }
}
