package com.gustavolyra.e_commerce_api.dto.user;

import com.gustavolyra.e_commerce_api.entities.User;
import lombok.Getter;

@Getter
public class UserDtoResponse {

    private Long id;
    private String name;

    public UserDtoResponse(User user) {
        this.id = user.getId();
        this.name = user.getUsername();
    }


}
