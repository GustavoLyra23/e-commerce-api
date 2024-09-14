package com.gustavolyra.e_commerce_api.factory;

import com.gustavolyra.e_commerce_api.entities.Role;
import com.gustavolyra.e_commerce_api.entities.User;

public class UserFactory {

    public static User createAdminUser() {
        var user = new User(1L, "gustavo@gmail.com", "123456");
        user.addRole(new Role(1L, "ROLE_ADMIN"));
        return user;
    }

    public static User createNonAdminUser() {
        var user = new User(2L, "david@gmail.com", "123456");
        user.addRole(new Role(2L, "ROLE_CLIENT"));
        return user;
    }

}
