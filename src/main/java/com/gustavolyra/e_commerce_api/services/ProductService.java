package com.gustavolyra.e_commerce_api.services;

import com.gustavolyra.e_commerce_api.dto.ProductDtoResponse;
import com.gustavolyra.e_commerce_api.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductDtoResponse> getAllProducts(Pageable pageable) {
        var products = productRepository.findAll(pageable);
        return products.map(ProductDtoResponse::new);
    }


}
