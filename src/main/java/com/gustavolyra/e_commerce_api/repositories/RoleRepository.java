package com.gustavolyra.e_commerce_api.repositories;

import com.gustavolyra.e_commerce_api.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
