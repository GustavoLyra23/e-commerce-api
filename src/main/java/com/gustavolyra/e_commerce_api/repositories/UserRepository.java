package com.gustavolyra.e_commerce_api.repositories;

import com.gustavolyra.e_commerce_api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
