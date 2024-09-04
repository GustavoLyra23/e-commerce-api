package com.gustavolyra.e_commerce_api.services;

import com.gustavolyra.e_commerce_api.dto.product.ProductDtoRequest;
import com.gustavolyra.e_commerce_api.dto.product.ProductDtoResponse;
import com.gustavolyra.e_commerce_api.entities.Product;
import com.gustavolyra.e_commerce_api.entities.ProductType;
import com.gustavolyra.e_commerce_api.repositories.ProductRepository;
import com.gustavolyra.e_commerce_api.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final UserService userService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductService(UserService userService, ProductRepository productRepository, UserRepository userRepository) {
        this.userService = userService;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductDtoResponse> getAllProducts(Pageable pageable) {
        var products = productRepository.findAll(pageable);
        return products.map(ProductDtoResponse::new);
    }

    @Transactional
    public ProductDtoResponse createProduct(@Valid ProductDtoRequest productDtoRequest) {
        var user = userService.findUserFromAuthenticationContext();

        Product product = new Product();
        product.setDescription(productDtoRequest.description());
        product.setName(productDtoRequest.name());
        product.setPrice(productDtoRequest.price());
        product.setType(ProductType.valueOf(productDtoRequest.type()));
        product.setUser(userRepository.getReferenceById(user.getId()));
        product = productRepository.save(product);
        return new ProductDtoResponse(product);
    }

}
