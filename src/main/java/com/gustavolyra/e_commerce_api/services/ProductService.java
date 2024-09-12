package com.gustavolyra.e_commerce_api.services;

import com.gustavolyra.e_commerce_api.dto.product.ProductDtoRequest;
import com.gustavolyra.e_commerce_api.dto.product.ProductDtoResponse;
import com.gustavolyra.e_commerce_api.entities.Product;
import com.gustavolyra.e_commerce_api.enums.ProductType;
import com.gustavolyra.e_commerce_api.repositories.ProductRepository;
import com.gustavolyra.e_commerce_api.repositories.UserRepository;
import com.gustavolyra.e_commerce_api.services.exceptions.ForbiddenException;
import com.gustavolyra.e_commerce_api.services.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;

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

    @Cacheable("products")
    @Transactional(readOnly = true)
    public Page<ProductDtoResponse> getAllProducts(Pageable pageable) {
        var products = productRepository.findAll(pageable);
        return products.map(ProductDtoResponse::new);
    }

    @CachePut(value = "products", key = "#result.id")
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

    @CacheEvict(value = "products", key = "#uuid")
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteProductById(UUID uuid) {
        var product = productRepository.findById(uuid).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        var user = userService.findUserFromAuthenticationContext();

        //goes to the user role list and return true if the user is an admin or false if he's not.
        boolean isUserAdmin = user.getAuthorities().stream()
                .anyMatch(x -> x.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));

        /*verifies if the user is non admin and if he's trying to delete other users product,
        if he's not an admin a forbidden exception will be thrown
         */
        if (!isUserAdmin && !product.getUser().getUsername().equalsIgnoreCase(user.getUsername())) {
            throw new ForbiddenException("Acess denied");
        }
        productRepository.deleteById(uuid);
    }

    @CachePut(value = "products", key = "#uuid")
    @Transactional()
    public ProductDtoResponse updateProduct(UUID uuid, @Valid ProductDtoRequest dtoRequest) throws IOException {
        var product = productRepository.findById(uuid).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productUpdateMapper(product, dtoRequest);
        product = productRepository.save(product);
        return new ProductDtoResponse(product);
    }


    private void productUpdateMapper(Product product, ProductDtoRequest dtoRequest) throws IOException {
        product.setPrice(dtoRequest.price());
        product.setType(ProductType.valueOf(dtoRequest.type()));
        var pictureUrl = s3Service.addFileToBucket(dtoRequest.file());
        product.setProductPictueUrl(pictureUrl);
        product.setName(dtoRequest.name());
        product.setDescription(dtoRequest.description());
        product.setPrice(dtoRequest.price());
    }


}
