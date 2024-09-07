package com.gustavolyra.e_commerce_api.repositories;

import com.gustavolyra.e_commerce_api.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query(nativeQuery = true, value = "DELETE FROM tb_cart")
    void deleteEverything();


}
