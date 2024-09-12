package com.gustavolyra.e_commerce_api.repositories;

import com.gustavolyra.e_commerce_api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByEmail(String email);

    @Query(nativeQuery = true, value = "SELECT COUNT(email) > 0 FROM tb_user WHERE email = :username")
    boolean existsByEmail(String username);


}
