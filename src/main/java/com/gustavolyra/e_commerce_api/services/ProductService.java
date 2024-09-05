package com.gustavolyra.e_commerce_api.services;

import com.gustavolyra.e_commerce_api.dto.product.ProductDtoRequest;
import com.gustavolyra.e_commerce_api.dto.product.ProductDtoResponse;
import com.gustavolyra.e_commerce_api.entities.Product;
import com.gustavolyra.e_commerce_api.entities.ProductType;
import com.gustavolyra.e_commerce_api.repositories.ProductRepository;
import com.gustavolyra.e_commerce_api.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
public class ProductService {

    private final S3Service s3Service;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductService(S3Service s3Service, UserService userService, ProductRepository productRepository, UserRepository userRepository) {
        this.s3Service = s3Service;
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
    public ProductDtoResponse createProduct(ProductDtoRequest dtoRequest) throws IOException {
        var user = userService.findUserFromAuthenticationContext();
        Product product = new Product();
        product.setDescription(dtoRequest.description());
        product.setName(dtoRequest.name());
        product.setPrice(dtoRequest.price());
        product.setType(ProductType.valueOf(dtoRequest.type()));
        product.setUser(userRepository.getReferenceById(user.getId()));

        String url = s3Service.addFileToBucket(dtoRequest.file());

        product.setProductPictueUrl(url);
        product = productRepository.save(product);
        return new ProductDtoResponse(product);
    }
}
