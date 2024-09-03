package com.gustavolyra.e_commerce_api;

import com.gustavolyra.e_commerce_api.entities.Product;
import com.gustavolyra.e_commerce_api.entities.ProductType;
import com.gustavolyra.e_commerce_api.repositories.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ECommerceApiApplication implements CommandLineRunner {

    private final ProductRepository productRepository;

    public ECommerceApiApplication(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(ECommerceApiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        productRepository.save(new Product(null, "PC", "Very good computer", 23.00,
                ProductType.ELETRONICS));
    }
}
