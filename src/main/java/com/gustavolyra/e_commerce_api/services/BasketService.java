package com.gustavolyra.e_commerce_api.services;

import com.gustavolyra.e_commerce_api.repositories.BasketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BasketService {

    private final BasketRepository basketRepository;


    public BasketService(BasketRepository basketRepository) {
        this.basketRepository = basketRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteBasketById(Long id) {
        basketRepository.deleteById(id);
    }


}
