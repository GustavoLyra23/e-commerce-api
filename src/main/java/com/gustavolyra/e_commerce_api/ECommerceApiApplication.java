package com.gustavolyra.e_commerce_api;

import com.gustavolyra.e_commerce_api.entities.Product;
import com.gustavolyra.e_commerce_api.entities.ProductType;
import com.gustavolyra.e_commerce_api.repositories.ProductRepository;
import com.gustavolyra.e_commerce_api.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@SpringBootApplication
public class ECommerceApiApplication implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public ECommerceApiApplication(UserRepository userRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(ECommerceApiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        productRepository.save(new Product(UUID.randomUUID(), "PC",
                "very good pc", 30.0, ProductType.ELETRONICS, userRepository.getReferenceById(1L)));
    }
}
