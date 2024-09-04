package com.gustavolyra.e_commerce_api.controllers;

import com.gustavolyra.e_commerce_api.dto.ProductDtoResponse;
import com.gustavolyra.e_commerce_api.services.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Page<ProductDtoResponse>> getAllProducts(Pageable pageable) {
        var products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

}
