package com.gustavolyra.e_commerce_api.repositories;

import com.gustavolyra.e_commerce_api.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
