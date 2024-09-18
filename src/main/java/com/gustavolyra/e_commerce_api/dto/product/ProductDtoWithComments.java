package com.gustavolyra.e_commerce_api.dto.product;

import com.gustavolyra.e_commerce_api.dto.comments.CommentDto;
import com.gustavolyra.e_commerce_api.entities.Product;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProductDtoWithComments extends ProductDtoResponse {
    private List<CommentDto> comments = new ArrayList<>();

    public ProductDtoWithComments(Product product) {
        super(product);
        if (product.getComments() != null) {
            this.comments = product.getComments().stream().map(CommentDto::new).toList();
        }
    }

}
