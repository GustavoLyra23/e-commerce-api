package com.gustavolyra.e_commerce_api;

import com.gustavolyra.e_commerce_api.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ECommerceApiApplication implements CommandLineRunner {

    private final UserRepository userRepository;

    public ECommerceApiApplication(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(ECommerceApiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        var user = userRepository.findById(1L);
        System.out.println(user.get().getAuthorities());
    }
}
