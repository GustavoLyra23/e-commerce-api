package com.gustavolyra.e_commerce_api.dto.user;

import com.gustavolyra.e_commerce_api.entities.User;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
public class UserDtoResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;

    public UserDtoResponse(User user) {
        this.id = user.getId();
        this.name = user.getUsername();
    }


}
