package com.gustavolyra.e_commerce_api.services;

import com.gustavolyra.e_commerce_api.dto.product.ProductDtoRequest;
import com.gustavolyra.e_commerce_api.dto.product.ProductDtoResponse;
import com.gustavolyra.e_commerce_api.dto.product.ProductDtoWithComments;
import com.gustavolyra.e_commerce_api.entities.Product;
import com.gustavolyra.e_commerce_api.enums.ProductType;
import com.gustavolyra.e_commerce_api.repositories.CommentRepository;
import com.gustavolyra.e_commerce_api.repositories.ProductRepository;
import com.gustavolyra.e_commerce_api.repositories.UserRepository;
import com.gustavolyra.e_commerce_api.services.exceptions.ForbiddenException;
import com.gustavolyra.e_commerce_api.services.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class ProductService {

    private final S3Service s3Service;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public ProductService(S3Service s3Service, UserService userService, ProductRepository productRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.s3Service = s3Service;
        this.userService = userService;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @Cacheable(value = "products", key = "#pageable.pageNumber + '-' + #pageable.pageSize + #name")
    @Transactional(readOnly = true)
    public Page<ProductDtoResponse> getAllProducts(String name, Pageable pageable) {
        if (name == null) {
            log.info("Received request to get all products");
            return productRepository.findAll(pageable).map(ProductDtoResponse::new);
        } else {
            log.info("Received request to get all products sorted by name containing {}", name);
            return productRepository.findByNameContainingIgnoreCase(name, pageable).map(ProductDtoResponse::new);
        }
    }

    @CacheEvict(value = "products", allEntries = true)
    @Transactional
    public ProductDtoResponse createProduct(ProductDtoRequest dtoRequest) throws IOException {
        var user = userService.findUserFromAuthenticationContext();
        log.info("User {} is creating a product", user.getUsername());
        Product product = new Product();
        product.setDescription(dtoRequest.description());
        product.setName(dtoRequest.name());
        product.setPrice(dtoRequest.price());
        product.setStock(dtoRequest.stock());
        try {
            product.setType(ProductType.valueOf(dtoRequest.type()));
        } catch (IllegalArgumentException e) {
            log.error("Could not find ENUM type for {}", dtoRequest.type());
            throw new BadRequestException("Type not found");
        }
        product.setUser(userRepository.getReferenceById(user.getId()));
        String url = s3Service.addFileToBucket(dtoRequest.file());
        product.setProductPictueUrl(url);
        product = productRepository.save(product);
        log.info("Product {} created successfully with ID {}", product.getName(), product.getUuid());
        return new ProductDtoResponse(product);

    }

    @CacheEvict(value = "products", allEntries = true)
    @Transactional()
    public void deleteProductById(UUID uuid) {
        log.info("Attempting request to delete product with ID {}: ", uuid);
        var product = productRepository.findById(uuid).orElseThrow(() -> {
            log.error("Product with ID {} not found:", uuid);
            return new ResourceNotFoundException("Product not found");
        });
        var user = userService.findUserFromAuthenticationContext();

        //goes to the user role list and return true if the user is an admin or false if he's not.
        boolean isUserAdmin = user.getAuthorities().stream()
                .anyMatch(x -> x.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));

        /*verifies if the user is non admin and if he's trying to delete other users product,
        if he's not an admin a forbidden exception will be thrown
         */
        if (!isUserAdmin && !product.getUser().getUsername().equalsIgnoreCase(user.getUsername())) {
            log.error("User {} attempted to delete product ID {} without permission", user.getUsername(), product.getUuid());
            throw new ForbiddenException("Acess denied");
        }
        productRepository.deleteById(uuid);
        log.info("Product with ID {} deleted successfully", uuid);
    }

    @CacheEvict(value = "products", allEntries = true)
    @Transactional()
    public ProductDtoResponse updateProduct(UUID uuid, ProductDtoRequest dtoRequest) throws IOException {
        log.info("Attempting to update product with ID {}", uuid);
        var product = productRepository.findById(uuid).orElseThrow(() -> {
            logErrorResourceNotFound(uuid);
            return new ResourceNotFoundException("Product not found");
        });

        var user = userService.findUserFromAuthenticationContext();
        boolean isUserAdmin = user.getAuthorities().stream()
                .anyMatch(x -> x.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));

        if (product.getUser().getUsername().equals(user.getUsername()) && !isUserAdmin) {
            log.error("User {} attempted to update product ID {} without permission", user.getUsername(), product.getUuid());
            throw new ForbiddenException("Access denied, cannot update other user's product");

        }


        productUpdateMapper(product, dtoRequest);
        product = productRepository.save(product);
        log.info("Product with ID {} successfully updated", uuid);
        return new ProductDtoResponse(product);
    }


    private void productUpdateMapper(Product product, ProductDtoRequest dtoRequest) throws IOException {
        product.setPrice(dtoRequest.price());
        product.setType(ProductType.valueOf(dtoRequest.type()));
        var pictureUrl = s3Service.addFileToBucket(dtoRequest.file());
        product.setProductPictueUrl(pictureUrl);
        product.setName(dtoRequest.name());
        product.setDescription(dtoRequest.description());
        product.setStock(dtoRequest.stock());
        product.setPrice(dtoRequest.price());
    }

    @Cacheable(value = "products", key = "#uuid")
    @Transactional(readOnly = true)
    public ProductDtoWithComments getProductById(UUID uuid) {
        log.info("Attempting to get product with ID {}", uuid);
        var product = productRepository.findByUuid(uuid).orElseThrow(() -> {
            logErrorResourceNotFound(uuid);
            return new ResourceNotFoundException("Product not found");
        });
        log.info("Product with ID {} found", uuid);
        return new ProductDtoWithComments(product);
    }

    private void logErrorResourceNotFound(UUID uuid) {
        log.error("Product with ID {} not found", uuid);
    }


}
