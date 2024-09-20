package com.gustavolyra.e_commerce_api.controllers;

import com.gustavolyra.e_commerce_api.dto.product.ProductDtoRequest;
import com.gustavolyra.e_commerce_api.dto.product.ProductDtoResponse;
import com.gustavolyra.e_commerce_api.dto.product.ProductDtoWithComments;
import com.gustavolyra.e_commerce_api.services.ProductService;
import com.gustavolyra.e_commerce_api.services.exceptions.DatabaseConflictException;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;


@RestController
@RequestMapping("/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<ProductDtoResponse>> getAllProducts(
            @RequestParam(defaultValue = "") String name, Pageable pageable) {
        var products = productService.getAllProducts(name, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDtoWithComments> getProductById(@PathVariable("id") UUID uuid) {
        var product = productService.getProductById(uuid);
        return ResponseEntity.ok(product);
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    public ResponseEntity<ProductDtoResponse> createProduct(@Valid @ModelAttribute ProductDtoRequest dtoRequest) throws IOException {
        var product = productService.createProduct(dtoRequest);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") UUID uuid) {
        try {
            productService.deleteProductById(uuid);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseConflictException("Product is associated with a basket");
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    public ResponseEntity<ProductDtoResponse> updateProduct(@PathVariable("id") UUID uuid,
                                                            @Valid @ModelAttribute ProductDtoRequest dtoRequest) throws IOException {
        var product = productService.updateProduct(uuid, dtoRequest);
        return ResponseEntity.ok(product);
    }
}
