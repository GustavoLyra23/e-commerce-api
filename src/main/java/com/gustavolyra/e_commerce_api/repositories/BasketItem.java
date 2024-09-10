package com.gustavolyra.e_commerce_api.repositories;

import com.gustavolyra.e_commerce_api.entities.Basket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketItem extends JpaRepository<Basket, Long> {
}
